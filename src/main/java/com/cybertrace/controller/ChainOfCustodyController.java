package com.cybertrace.controller;

import com.cybertrace.dto.ChainOfCustodyRequest;
import com.cybertrace.model.ChainOfCustody;
import com.cybertrace.model.Evidence;
import com.cybertrace.model.User;
import com.cybertrace.service.ChainOfCustodyService;
import com.cybertrace.service.EvidenceService;
import com.cybertrace.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chain-of-custody")
@RequiredArgsConstructor
public class ChainOfCustodyController {

    private final ChainOfCustodyService custodyService;
    private final EvidenceService evidenceService;
    private final UserService userService;

    @PostMapping("/evidence/{evidenceId}")
    public ResponseEntity<ChainOfCustody> addRecord(@PathVariable Long evidenceId,
                                                       @Valid @RequestBody ChainOfCustodyRequest request,
                                                       Authentication authentication) {
        Evidence evidence = evidenceService.getEvidenceById(evidenceId);
        User handler = userService.getByEmail(authentication.getName());
        return ResponseEntity.ok(custodyService.addRecord(evidence, request, handler));
    }

    @GetMapping("/evidence/{evidenceId}")
    public ResponseEntity<List<ChainOfCustody>> getHistory(@PathVariable Long evidenceId) {
        return ResponseEntity.ok(custodyService.getHistoryForEvidence(evidenceId));
    }
}
