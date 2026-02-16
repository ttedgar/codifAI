package com.edi.backend.service;

import com.edi.backend.dto.Judge0ResultResponse;
import com.edi.backend.dto.SubmissionResponse;
import com.edi.backend.entity.Challenge;
import com.edi.backend.entity.Submission;
import com.edi.backend.entity.SubmissionStatus;
import com.edi.backend.entity.User;
import com.edi.backend.repository.ChallengeRepository;
import com.edi.backend.repository.SubmissionRepository;
import com.edi.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeExecutionService {

    private final Judge0Service judge0Service;
    private final SubmissionRepository submissionRepository;
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    /**
     * Evaluate a code submission against challenge test cases
     */
    @Transactional
    public SubmissionResponse evaluateSubmission(Long userId, Long challengeId, String userCode) {
        log.info("Evaluating submission for user {} on challenge {}", userId, challengeId);

        // Fetch challenge
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        // Combine user code with hidden tests
        String combinedCode = buildExecutableCode(userCode, challenge.getHiddenTests());

        // Execute via Judge0
        Judge0ResultResponse result;
        try {
            result = judge0Service.executeAndWait(combinedCode, "");
        } catch (Exception e) {
            log.error("Judge0 execution failed", e);
            return createFailedSubmission(userId, challengeId, userCode, "Execution service unavailable");
        }

        // Parse result and determine status
        SubmissionStatus status = determineStatus(result);

        // Create submission record
        Submission submission = Submission.builder()
                .userId(userId)
                .challengeId(challengeId)
                .code(userCode)
                .status(status)
                .stdout(result.getStdout())
                .stderr(result.getStderr())
                .executionTime(parseExecutionTime(result.getTime()))
                .memory(result.getMemory())
                .build();

        // Award XP if all tests passed (check BEFORE saving submission)
        boolean shouldAwardXP = false;
        if (status == SubmissionStatus.ACCEPTED) {
            shouldAwardXP = !submissionRepository.existsByUserIdAndChallengeIdAndStatus(
                    userId, challengeId, SubmissionStatus.ACCEPTED
            );
        }

        submission = submissionRepository.save(submission);

        if (shouldAwardXP) {
            awardXP(userId, challenge);
        }

        return mapToResponse(submission);
    }

    /**
     * Build executable code by combining user solution with test cases
     */
    private String buildExecutableCode(String userCode, String hiddenTests) {
        return """
                %s

                // Hidden test cases
                %s
                """.formatted(userCode, hiddenTests);
    }

    /**
     * Determine submission status from Judge0 result
     */
    private SubmissionStatus determineStatus(Judge0ResultResponse result) {
        Integer statusId = result.getStatus().getId();

        return switch (statusId) {
            case 3 -> SubmissionStatus.ACCEPTED;
            case 4 -> SubmissionStatus.WRONG_ANSWER;
            case 5 -> SubmissionStatus.TIME_LIMIT_EXCEEDED;
            case 6 -> SubmissionStatus.COMPILATION_ERROR;
            default -> SubmissionStatus.RUNTIME_ERROR;
        };
    }

    /**
     * Parse execution time from Judge0 response
     */
    private Integer parseExecutionTime(String time) {
        if (time == null || time.isEmpty()) {
            return null;
        }
        try {
            return (int) (Double.parseDouble(time) * 1000); // Convert seconds to milliseconds
        } catch (NumberFormatException e) {
            log.warn("Failed to parse execution time: {}", time);
            return null;
        }
    }

    /**
     * Award XP to user for solving a challenge
     */
    private void awardXP(Long userId, Challenge challenge) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int xpToAward = switch (challenge.getDifficulty()) {
            case EASY -> 10;
            case MEDIUM -> 25;
            case HARD -> 50;
        };

        user.setXp(user.getXp() + xpToAward);
        userRepository.save(user);

        log.info("Awarded {} XP to user {} for solving challenge {}", xpToAward, userId, challenge.getId());
    }

    /**
     * Create a failed submission when execution service is unavailable
     */
    private SubmissionResponse createFailedSubmission(Long userId, Long challengeId, String code, String errorMessage) {
        Submission submission = Submission.builder()
                .userId(userId)
                .challengeId(challengeId)
                .code(code)
                .status(SubmissionStatus.RUNTIME_ERROR)
                .stdout(null)
                .stderr(errorMessage)
                .executionTime(null)
                .memory(null)
                .build();

        submission = submissionRepository.save(submission);
        return mapToResponse(submission);
    }

    /**
     * Map Submission entity to SubmissionResponse DTO
     */
    private SubmissionResponse mapToResponse(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .userId(submission.getUserId())
                .challengeId(submission.getChallengeId())
                .status(submission.getStatus())
                .stdout(submission.getStdout())
                .stderr(submission.getStderr())
                .executionTime(submission.getExecutionTime())
                .memory(submission.getMemory())
                .createdAt(submission.getCreatedAt())
                .build();
    }
}
