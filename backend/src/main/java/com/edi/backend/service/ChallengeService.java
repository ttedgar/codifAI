package com.edi.backend.service;

import com.edi.backend.dto.ChallengeGenerationRequest;
import com.edi.backend.dto.ChallengeRequest;
import com.edi.backend.dto.ChallengeResponse;
import com.edi.backend.dto.PageResponse;
import com.edi.backend.entity.Challenge;
import com.edi.backend.exception.ChallengeNotFoundException;
import com.edi.backend.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final AiChallengeGenerator aiChallengeGenerator;

    @Transactional(readOnly = true)
    public PageResponse<ChallengeResponse> getAllChallenges(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        Page<Challenge> challengePage = challengeRepository.findAll(pageable);

        return PageResponse.<ChallengeResponse>builder()
                .content(challengePage.getContent().stream()
                        .map(this::mapToResponse)
                        .toList())
                .page(challengePage.getNumber())
                .size(challengePage.getSize())
                .totalElements(challengePage.getTotalElements())
                .totalPages(challengePage.getTotalPages())
                .last(challengePage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public ChallengeResponse getChallengeById(Long id) {
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new ChallengeNotFoundException(id));
        return mapToResponse(challenge);
    }

    @Transactional
    public ChallengeResponse createChallenge(ChallengeRequest request) {
        List<String> existingTitles = challengeRepository.findAllTitles();

        ChallengeGenerationRequest generated = aiChallengeGenerator.generateChallenge(
                request.getPrompt(),
                request.getDifficulty(),
                existingTitles
        );

        Challenge challenge = Challenge.builder()
                .title(generated.getTitle())
                .description(generated.getDescription())
                .difficulty(request.getDifficulty())
                .starterCode(generated.getStarterCode())
                .hiddenTests(generated.getHiddenTests())
                .sampleTests(generated.getSampleTests())
                .tags(generated.getTags())
                .build();

        challenge = challengeRepository.save(challenge);
        return mapToResponse(challenge);
    }

    @Transactional
    public void deleteChallenge(Long id) {
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new ChallengeNotFoundException(id));

        challengeRepository.delete(challenge);
    }

    private ChallengeResponse mapToResponse(Challenge challenge) {
        return ChallengeResponse.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .difficulty(challenge.getDifficulty())
                .starterCode(challenge.getStarterCode())
                .sampleTests(challenge.getSampleTests())
                .tags(challenge.getTags())
                .createdAt(challenge.getCreatedAt())
                .build();
    }
}
