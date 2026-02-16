package com.edi.backend.dto;

import com.edi.backend.entity.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChallengeRequest {
    @NotBlank(message = "Prompt is required")
    @Size(min = 10, max = 500, message = "Prompt must be between 10 and 500 characters")
    private String prompt;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;
}
