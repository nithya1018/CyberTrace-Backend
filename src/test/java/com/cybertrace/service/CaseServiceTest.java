package com.cybertrace.service;

import com.cybertrace.dto.CaseRequest;
import com.cybertrace.model.ForensicCase;
import com.cybertrace.model.Role;
import com.cybertrace.model.RoleName;
import com.cybertrace.model.User;
import com.cybertrace.repository.CaseRepository;
import com.cybertrace.repository.FindingRepository;
import com.cybertrace.repository.InvestigationNoteRepository;
import com.cybertrace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {

    @Mock private CaseRepository caseRepository;
    @Mock private UserRepository userRepository;
    @Mock private AuditLogService auditLogService;
    @Mock private InvestigationNoteRepository investigationNoteRepository;
    @Mock private FindingRepository findingRepository;

    @InjectMocks private CaseService caseService;

    @Test
    void createCaseRequiresAnInvestigator() {
        CaseRequest request = new CaseRequest();
        request.setTitle("Incident");

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> caseService.createCase(request, User.builder().id(10L).build()));

        assertEquals("Choose an investigator before creating a case", error.getMessage());
        verify(caseRepository, never()).save(any());
    }

    @Test
    void createCaseAssignsSelectedInvestigator() {
        User investigator = User.builder()
                .id(22L)
                .name("Investigator")
                .enabled(true)
                .role(Role.builder().name(RoleName.INVESTIGATOR).build())
                .build();
        User admin = User.builder().id(10L).build();
        CaseRequest request = new CaseRequest();
        request.setTitle("Incident");
        request.setAssignedToUserId(investigator.getId());
        when(userRepository.findById(investigator.getId())).thenReturn(Optional.of(investigator));
        when(caseRepository.save(any(ForensicCase.class))).thenAnswer(invocation -> {
            ForensicCase saved = invocation.getArgument(0);
            saved.setId(101L);
            return saved;
        });

        ForensicCase created = caseService.createCase(request, admin);

        assertSame(investigator, created.getAssignedTo());
        verify(caseRepository).save(created);
        verify(auditLogService).log(admin, "CREATE_CASE", null, "Created case #101: Incident");
    }

    @Test
    void createCaseRejectsDisabledInvestigator() {
        User investigator = User.builder()
                .id(22L)
                .enabled(false)
                .role(Role.builder().name(RoleName.INVESTIGATOR).build())
                .build();
        CaseRequest request = new CaseRequest();
        request.setTitle("Incident");
        request.setAssignedToUserId(investigator.getId());
        when(userRepository.findById(investigator.getId())).thenReturn(Optional.of(investigator));

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> caseService.createCase(request, User.builder().id(10L).build()));

        assertEquals("Selected investigator is disabled", error.getMessage());
        verify(caseRepository, never()).save(any());
    }

    @Test
    void investigatorReadsAreScopedToTheirUserId() {
        List<ForensicCase> assignedCases = List.of(ForensicCase.builder().id(5L).build());
        when(caseRepository.findByAssignedTo_Id(22L)).thenReturn(assignedCases);

        assertSame(assignedCases, caseService.getCasesForInvestigator(22L));
        verify(caseRepository).findByAssignedTo_Id(22L);
    }
}
