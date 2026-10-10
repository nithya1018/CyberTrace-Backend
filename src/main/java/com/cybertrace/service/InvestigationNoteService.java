package com.cybertrace.service;

import com.cybertrace.dto.NoteRequest;
import com.cybertrace.model.*;
import com.cybertrace.repository.InvestigationNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestigationNoteService {

    private final InvestigationNoteRepository noteRepository;

    public InvestigationNote addNote(ForensicCase forensicCase, NoteRequest request, User author) {
        InvestigationNote note = InvestigationNote.builder()
                .forensicCase(forensicCase)
                .createdBy(author)
                .note(request.getNote())
                .build();

        return noteRepository.save(note);
    }

    public List<InvestigationNote> getNotesForCase(Long caseId) {
        return noteRepository.findByForensicCase_IdOrderByCreatedAtDesc(caseId);
    }
}
