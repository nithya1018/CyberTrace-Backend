package com.cybertrace.repository;

import com.cybertrace.model.ForensicCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseRepository extends JpaRepository<ForensicCase, Long> {
    List<ForensicCase> findByAssignedTo_Id(Long userId);
}