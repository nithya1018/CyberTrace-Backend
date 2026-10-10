package com.cybertrace.controller;

import com.cybertrace.dto.FindingRequest;
import com.cybertrace.dto.NoteRequest;
import com.cybertrace.model.Finding;
import com.cybertrace.model.ForensicCase;
import com.cybertrace.model.InvestigationNote;
import com.cybertrace.model.User;
import com.cybertrace.service.CaseService;
import com.cybertrace.service.FindingService;
import com.cybertrace.service.InvestigationNoteService;
import com.cybertrace.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investigation")
@RequiredArgsConstructor
public class InvestigationController {

    private final InvestigationNoteService noteService;
    private final FindingService findingService;
    private final CaseService caseService;
    private final UserService userService;

    @PostMapping("/case/{caseId}/notes")
    public ResponseEntity<InvestigationNote> addNote(@PathVariable Long caseId,
                                                        @Valid @RequestBody NoteRequest request,
                                                        Authentication authentication) {
        ForensicCase forensicCase = caseService.getCaseById(caseId);
        User author = userService.getByEmail(authentication.getName());
        return ResponseEntity.ok(noteService.addNote(forensicCase, request, author));
    }

    @GetMapping("/case/{caseId}/notes")
    public ResponseEntity<List<InvestigationNote>> getNotes(@PathVariable Long caseId) {
        return ResponseEntity.ok(noteService.getNotesForCase(caseId));
    }

    @PostMapping("/case/{caseId}/findings")
    public ResponseEntity<Finding> addFinding(@PathVariable Long caseId,
                                                @Valid @RequestBody FindingRequest request,
                                                Authentication authentication) {
        ForensicCase forensicCase = caseService.getCaseById(caseId);
        User author = userService.getByEmail(authentication.getName());
        return ResponseEntity.ok(findingService.addFinding(forensicCase, request, author));
    }

    @GetMapping("/case/{caseId}/findings")
    public ResponseEntity<List<Finding>> getFindings(@PathVariable Long caseId) {
        return ResponseEntity.ok(findingService.getFindingsForCase(caseId));
    }
}
