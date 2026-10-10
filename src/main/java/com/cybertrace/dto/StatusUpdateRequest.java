package com.cybertrace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    @NotBlank
    private String status; // OPEN, IN_PROGRESS, CLOSED
}
