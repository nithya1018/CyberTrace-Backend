package com.cybertrace.repository;

import com.cybertrace.model.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    List<Evidence> findByForensicCase_Id(Long caseId);
}
