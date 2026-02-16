package com.edi.backend.dto;

import com.edi.backend.entity.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {
    private Long id;
    private Long userId;
    private Long challengeId;
    private SubmissionStatus status;
    private String stdout;
    private String stderr;
    private Integer executionTime;
    private Integer memory;
    private LocalDateTime createdAt;
}
