package com.cybertrace.repository;

import com.cybertrace.model.InvestigationNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvestigationNoteRepository
        extends JpaRepository<InvestigationNote, Long> {

    List<InvestigationNote> findByForensicCase_IdOrderByCreatedAtDesc(Long caseId);

    @Modifying
    @Query("DELETE FROM InvestigationNote n WHERE n.forensicCase.id = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}