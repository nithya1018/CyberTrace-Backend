package com.cybertrace.controller;

import com.cybertrace.dto.CaseRequest;
import com.cybertrace.dto.StatusUpdateRequest;
import com.cybertrace.model.ForensicCase;
import com.cybertrace.model.RoleName;
import com.cybertrace.model.User;
import com.cybertrace.repository.UserRepository;
import com.cybertrace.service.CaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;
    private final UserRepository userRepository;

    private User currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
    }

    private void requireAdmin(Authentication auth) {
        if (!isAdmin(auth)) throw new AccessDeniedException("Admin only");
    }

    private void requireAccess(ForensicCase c, Authentication auth) {
        if (isAdmin(auth)) return;
        if (c.getAssignedTo() == null || !c.getAssignedTo().getEmail().equals(auth.getName())) {
            throw new AccessDeniedException("This case is not assigned to you");
        }
    }

    // CREATE (admin) - an investigator must be assigned to every new case
    @PostMapping
    public ForensicCase create(@Valid @RequestBody CaseRequest request, Authentication auth) {
        requireAdmin(auth);
        if (request.getAssignedToUserId() == null) {
            throw new IllegalArgumentException("Choose an investigator before creating a case");
        }
        return caseService.createCase(request, currentUser(auth));
    }

    // READ ALL (admin: all cases, investigator: only theirs)
    @GetMapping
    public List<ForensicCase> list(Authentication auth) {
        if (isAdmin(auth)) return caseService.getAllCases();
        return caseService.getCasesForInvestigator(currentUser(auth).getId());
    }

    // dropdown data for "assign investigator" (admin)
    @GetMapping("/investigators")
    public List<User> investigators(Authentication auth) {
        requireAdmin(auth);
        return userRepository.findByRole_Name(RoleName.INVESTIGATOR).stream()
                .filter(User::isEnabled)
                .toList();
    }

    // READ ONE
    @GetMapping("/{id}")
    public ForensicCase get(@PathVariable Long id, Authentication auth) {
        ForensicCase c = caseService.getCaseById(id);
        requireAccess(c, auth);
        return c;
    }

    // UPDATE (admin, or the assigned investigator)
    @PutMapping("/{id}")
    public ForensicCase update(@PathVariable Long id, @Valid @RequestBody CaseRequest request,
                               Authentication auth) {
        requireAccess(caseService.getCaseById(id), auth);
        return caseService.updateCase(id, request, currentUser(auth), isAdmin(auth));
    }

    // ASSIGN / REASSIGN (admin)   body: {"investigatorId": 2}
    @PatchMapping("/{id}/assign")
    public ForensicCase assign(@PathVariable Long id, @RequestBody Map<String, Long> body,
                               Authentication auth) {
        requireAdmin(auth);
        return caseService.assignCase(id, body.get("investigatorId"), currentUser(auth));
    }

    // STATUS (kept; admin or the assigned investigator)
    @PatchMapping("/{id}/status")
    public ForensicCase updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request,
                                     Authentication auth) {
        requireAccess(caseService.getCaseById(id), auth);
        return caseService.updateStatus(id, request, currentUser(auth));
    }

    // DELETE (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        requireAdmin(auth);
        caseService.deleteCase(id, currentUser(auth));
        return ResponseEntity.noContent().build();
    }
}
