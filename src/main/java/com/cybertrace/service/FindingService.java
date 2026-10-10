package com.cybertrace.service;

import com.cybertrace.dto.FindingRequest;
import com.cybertrace.model.*;
import com.cybertrace.repository.FindingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindingService {

    private final FindingRepository findingRepository;

    public Finding addFinding(ForensicCase forensicCase, FindingRequest request, User author) {
        Finding finding = Finding.builder()
                .forensicCase(forensicCase)
                .summary(request.getSummary())
                .recommendation(request.getRecommendation())
                .createdBy(author)
                .build();

        return findingRepository.save(finding);
    }

    public List<Finding> getFindingsForCase(Long caseId) {
        return findingRepository.findByForensicCase_Id(caseId);
    }
}
