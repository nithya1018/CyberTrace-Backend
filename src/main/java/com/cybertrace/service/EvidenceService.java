package com.cybertrace.service;

import com.cybertrace.model.*;
import com.cybertrace.repository.EvidenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final AuditLogService auditLogService;
    private static final String UPLOAD_DIR = "uploads/evidence";

    public Evidence uploadEvidence(ForensicCase forensicCase, MultipartFile file,
                                    String description, User uploader) throws IOException {
        Files.createDirectories(Paths.get(UPLOAD_DIR));

        String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, storedFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String hash = computeSha256(filePath);

        Evidence evidence = Evidence.builder()
                .forensicCase(forensicCase)
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .description(description)
                .hash(hash)
                .storagePath(filePath.toString())
                .uploadedBy(uploader)
                .build();

        Evidence saved = evidenceRepository.save(evidence);
        auditLogService.log(uploader, "UPLOAD_EVIDENCE", null,
                "Uploaded \"" + saved.getFileName() + "\" to case #" + forensicCase.getId()
                        + " (SHA-256: " + hash.substring(0, 16) + "...)");
        return saved;
    }

    private String computeSha256(Path filePath) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(filePath);
            byte[] hashBytes = digest.digest(fileBytes);
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public List<Evidence> getEvidenceForCase(Long caseId) {
        return evidenceRepository.findByForensicCase_Id(caseId);
    }

    public Evidence getEvidenceById(Long id) {
        return evidenceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + id));
    }
}
