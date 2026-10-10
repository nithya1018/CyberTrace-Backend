package com.cybertrace.controller;

import com.cybertrace.model.Evidence;
import com.cybertrace.model.ForensicCase;
import com.cybertrace.model.User;
import com.cybertrace.service.CaseService;
import com.cybertrace.service.EvidenceService;
import com.cybertrace.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/evidence")
@RequiredArgsConstructor
public class EvidenceController {

    private final EvidenceService evidenceService;
    private final CaseService caseService;
    private final UserService userService;

    @PostMapping(value = "/case/{caseId}", consumes = "multipart/form-data")
    public ResponseEntity<Evidence> uploadEvidence(@PathVariable Long caseId,
                                                     @RequestParam("file") MultipartFile file,
                                                     @RequestParam(value = "description", required = false) String description,
                                                     Authentication authentication) throws IOException {
        ForensicCase forensicCase = caseService.getCaseById(caseId);
        User uploader = userService.getByEmail(authentication.getName());
        Evidence evidence = evidenceService.uploadEvidence(forensicCase, file, description, uploader);
        return ResponseEntity.ok(evidence);
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<Evidence>> getEvidenceForCase(@PathVariable Long caseId) {
        return ResponseEntity.ok(evidenceService.getEvidenceForCase(caseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evidence> getEvidence(@PathVariable Long id) {
        return ResponseEntity.ok(evidenceService.getEvidenceById(id));
    }
}
