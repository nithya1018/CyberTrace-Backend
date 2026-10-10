package com.cybertrace.service;

import com.cybertrace.dto.ChainOfCustodyRequest;
import com.cybertrace.model.*;
import com.cybertrace.repository.ChainOfCustodyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChainOfCustodyService {

    private final ChainOfCustodyRepository custodyRepository;
    private final AuditLogService auditLogService;

    public ChainOfCustody addRecord(Evidence evidence, ChainOfCustodyRequest request, User handler) {
        ChainOfCustody record = ChainOfCustody.builder()
                .evidence(evidence)
                .handledBy(handler)
                .action(request.getAction())
                .location(request.getLocation())
                .notes(request.getNotes())
                .build();

        ChainOfCustody saved = custodyRepository.save(record);
        auditLogService.log(handler, "CHAIN_OF_CUSTODY", null,
                request.getAction() + " — evidence #" + evidence.getId());
        return saved;
    }

    public List<ChainOfCustody> getHistoryForEvidence(Long evidenceId) {
        return custodyRepository.findByEvidence_IdOrderByTimestampAsc(evidenceId);
    }
}
