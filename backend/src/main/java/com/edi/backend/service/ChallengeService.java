package com.edi.backend.service;

import com.edi.backend.dto.ChallengeGenerationRequest;
import com.edi.backend.dto.ChallengeRequest;
import com.edi.backend.dto.ChallengeResponse;
import com.edi.backend.dto.PageResponse;
import com.edi.backend.entity.Challenge;
import com.edi.backend.entity.Role;
import com.edi.backend.entity.User;
import com.edi.backend.repository.ChallengeRepository;
import com.edi.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;

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
                .orElseThrow(() -> new RuntimeException("Challenge not found with id: " + id));
        return mapToResponse(challenge);
    }

    @Transactional
    public ChallengeResponse createChallenge(ChallengeRequest request) {
        // Get existing challenge titles to avoid duplicates
        List<String> existingTitles = challengeRepository.findAllTitles();

        // Call Gemini to generate challenge
        ChallengeGenerationRequest generated = geminiService.generateChallenge(
                request.getPrompt(),
                request.getDifficulty(),
                existingTitles
        );

        // Create and save challenge
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
                .orElseThrow(() -> new RuntimeException("Challenge not found with id: " + id));

        // Only ADMIN can delete challenges
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only administrators can delete challenges");
        }

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
