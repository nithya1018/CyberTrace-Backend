package com.cybertrace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChainOfCustodyRequest {
    @NotBlank
    private String action;
    private String location;
    private String notes;
}
