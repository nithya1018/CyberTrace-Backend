package com.cybertrace.dto;

import com.cybertrace.model.CaseStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CaseRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private CaseStatus status;            // optional: OPEN, IN_PROGRESS, CLOSED

    private Long assignedToUserId;        // investigator's user id (optional)
}