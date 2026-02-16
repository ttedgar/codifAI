package com.edi.backend.dto;

import com.edi.backend.entity.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponse {
    private Long id;
    private String title;
    private String description;
    private Difficulty difficulty;
    private String starterCode;
    private String sampleTests;
    private List<String> tags;
    private LocalDateTime createdAt;
}
