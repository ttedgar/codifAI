package com.edi.backend.service;

import com.edi.backend.dto.ChallengeGenerationRequest;
import com.edi.backend.entity.Difficulty;

import java.util.List;

public interface AiChallengeGenerator {
    ChallengeGenerationRequest generateChallenge(String prompt, Difficulty difficulty, List<String> existingTitles);
}
