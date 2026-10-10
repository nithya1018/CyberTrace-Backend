package com.cybertrace.repository;

import com.cybertrace.model.Finding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FindingRepository extends JpaRepository<Finding, Long> {

    List<Finding> findByForensicCase_Id(Long caseId);

    @Modifying
    @Query("DELETE FROM Finding f WHERE f.forensicCase.id = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}