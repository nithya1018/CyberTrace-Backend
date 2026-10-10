package com.cybertrace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FindingRequest {
    @NotBlank
    private String summary;
    private String recommendation;
}
