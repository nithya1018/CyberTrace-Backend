package com.cybertrace.repository;

import com.cybertrace.model.ChainOfCustody;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChainOfCustodyRepository extends JpaRepository<ChainOfCustody, Long> {
    List<ChainOfCustody> findByEvidence_IdOrderByTimestampAsc(Long evidenceId);
}
