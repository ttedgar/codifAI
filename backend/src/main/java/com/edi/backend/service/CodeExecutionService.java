package com.edi.backend.service;

import com.edi.backend.dto.CodeExecutionResult;
import com.edi.backend.dto.SubmissionResponse;
import com.edi.backend.entity.Challenge;
import com.edi.backend.entity.Submission;
import com.edi.backend.entity.SubmissionStatus;
import com.edi.backend.entity.User;
import com.edi.backend.repository.ChallengeRepository;
import com.edi.backend.repository.SubmissionRepository;
import com.edi.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeExecutionService {

    private final CodeExecutor codeExecutor;
    private final SubmissionRepository submissionRepository;
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    @Transactional
    public SubmissionResponse evaluateSubmission(Long userId, Long challengeId, String userCode) {
        log.info("Evaluating submission for user {} on challenge {}", userId, challengeId);

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        String combinedCode = buildExecutableCode(userCode, challenge.getHiddenTests());

        CodeExecutionResult result;
        try {
            result = codeExecutor.execute(combinedCode, "");
        } catch (Exception e) {
            log.error("Code execution failed", e);
            return createFailedSubmission(userId, challengeId, userCode, "Execution service unavailable");
        }

        SubmissionStatus status = determineStatus(result);

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

    private String buildExecutableCode(String userCode, String hiddenTests) {
        return """
                %s

                // Hidden test cases
                %s
                """.formatted(userCode, hiddenTests);
    }

    private SubmissionStatus determineStatus(CodeExecutionResult result) {
        Integer statusId = result.getStatusId();

        if (statusId == 6) {
            return SubmissionStatus.COMPILATION_ERROR;
        }
        if (statusId == 5) {
            return SubmissionStatus.TIME_LIMIT_EXCEEDED;
        }
        if (statusId != 3) {
            return SubmissionStatus.RUNTIME_ERROR;
        }

        TestResult testResult = parseTestOutput(result.getStdout());

        if (testResult.getFailed() > 0) {
            return SubmissionStatus.WRONG_ANSWER;
        }

        if (testResult.getPassed() > 0) {
            return SubmissionStatus.ACCEPTED;
        }

        return SubmissionStatus.RUNTIME_ERROR;
    }

    private TestResult parseTestOutput(String stdout) {
        if (stdout == null || stdout.isEmpty()) {
            return new TestResult(0, 0);
        }

        long passCount = stdout.lines()
                .filter(line -> line.trim().startsWith("PASS"))
                .count();

        long failCount = stdout.lines()
                .filter(line -> line.trim().startsWith("FAIL"))
                .count();

        return new TestResult((int) passCount, (int) failCount);
    }

    @Data
    @AllArgsConstructor
    private static class TestResult {
        int passed;
        int failed;
    }

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
