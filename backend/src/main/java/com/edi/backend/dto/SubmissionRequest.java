package com.edi.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionRequest {
    @NotNull(message = "Challenge ID is required")
    private Long challengeId;

    @NotBlank(message = "Code is required")
    private String code;
}
