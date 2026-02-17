package com.edi.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeGenerationRequest {
    private String title;
    private String description;
    private String starterCode;
    private String hiddenTests;
    private String sampleTests;
    private List<String> tags;
}
