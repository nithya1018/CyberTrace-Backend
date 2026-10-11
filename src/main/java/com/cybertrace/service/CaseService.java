package com.cybertrace.service;

import com.cybertrace.dto.CaseRequest;
import com.cybertrace.dto.StatusUpdateRequest;
import com.cybertrace.model.*;
import com.cybertrace.repository.CaseRepository;
import com.cybertrace.repository.UserRepository;
import com.cybertrace.repository.InvestigationNoteRepository;
import com.cybertrace.repository.FindingRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final InvestigationNoteRepository investigationNoteRepository;
    private final FindingRepository findingRepository;

    private User findInvestigator(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Assigned user not found"));

        if (user.getRole().getName() != RoleName.INVESTIGATOR) {
            throw new IllegalArgumentException(
                    "Selected user is not an investigator");
        }

        if (!user.isEnabled()) {
            throw new IllegalArgumentException(
                    "Selected investigator is disabled");
        }

        return user;
    }

    // CREATE
    public ForensicCase createCase(CaseRequest request, User creator) {

        if (request.getAssignedToUserId() == null) {
            throw new IllegalArgumentException("Choose an investigator before creating a case");
        }
        User assignedTo = findInvestigator(request.getAssignedToUserId());

        ForensicCase forensicCase = ForensicCase.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null
                        ? request.getStatus()
                        : CaseStatus.OPEN)
                .assignedTo(assignedTo)
                .createdBy(creator)
                .build();

        ForensicCase saved = caseRepository.save(forensicCase);

        auditLogService.log(
                creator,
                "CREATE_CASE",
                null,
                "Created case #" + saved.getId()
                        + ": " + saved.getTitle()
        );

        return saved;
    }

    // READ
    public List<ForensicCase> getAllCases() {
        return caseRepository.findAll();
    }

    public ForensicCase getCaseById(Long id) {
        return caseRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Case not found: " + id));
    }

    public List<ForensicCase> getCasesForInvestigator(Long userId) {
        return caseRepository.findByAssignedTo_Id(userId);
    }

    // UPDATE
    public ForensicCase updateCase(
            Long id,
            CaseRequest request,
            User actor,
            boolean canReassign) {

        ForensicCase forensicCase = getCaseById(id);

        forensicCase.setTitle(request.getTitle());
        forensicCase.setDescription(request.getDescription());

        if (request.getStatus() != null) {
            forensicCase.setStatus(request.getStatus());
        }

        if (canReassign && request.getAssignedToUserId() != null) {
            forensicCase.setAssignedTo(
                    findInvestigator(request.getAssignedToUserId()));
        }

        ForensicCase saved = caseRepository.save(forensicCase);

        auditLogService.log(
                actor,
                "UPDATE_CASE",
                null,
                "Updated case #" + id
        );

        return saved;
    }

    // ASSIGN / REASSIGN
    public ForensicCase assignCase(
            Long id,
            Long investigatorId,
            User actor) {

        ForensicCase forensicCase = getCaseById(id);

        forensicCase.setAssignedTo(findInvestigator(investigatorId));

        ForensicCase saved = caseRepository.save(forensicCase);

        auditLogService.log(
                actor,
                "ASSIGN_CASE",
                null,
                "Case #" + id + " assigned to "
                        + saved.getAssignedTo().getName()
        );

        return saved;
    }

    // STATUS
    public ForensicCase updateStatus(
            Long id,
            StatusUpdateRequest request,
            User actor) {

        ForensicCase forensicCase = getCaseById(id);

        CaseStatus oldStatus = forensicCase.getStatus();

        forensicCase.setStatus(
                CaseStatus.valueOf(request.getStatus().toUpperCase()));

        ForensicCase saved = caseRepository.save(forensicCase);

        auditLogService.log(
                actor,
                "UPDATE_STATUS",
                null,
                "Case #" + id + " status: "
                        + oldStatus + " -> " + saved.getStatus()
        );

        return saved;
    }

    // DELETE
    @Transactional
    public void deleteCase(Long id, User actor) {

        ForensicCase forensicCase = getCaseById(id);
        String title = forensicCase.getTitle();

        // Delete investigation notes associated with the case
        investigationNoteRepository.deleteByCaseId(id);

        // Delete findings associated with the case
        findingRepository.deleteByCaseId(id);

        // Delete the case
        caseRepository.delete(forensicCase);

        // Record the deletion
        auditLogService.log(
                actor,
                "DELETE_CASE",
                null,
                "Deleted case #" + id + ": " + title
        );
    }
}
