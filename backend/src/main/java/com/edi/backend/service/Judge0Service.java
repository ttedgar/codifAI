package com.edi.backend.service;

import com.edi.backend.dto.Judge0ResultResponse;
import com.edi.backend.dto.Judge0SubmissionRequest;
import com.edi.backend.dto.Judge0SubmissionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@Slf4j
public class Judge0Service {

    private final WebClient webClient;
    private final Integer languageId;
    private final Integer timeout;
    private final Integer maxPollAttempts;
    private final Integer pollInterval;

    public Judge0Service(
            @Value("${judge0.base-url}") String baseUrl,
            @Value("${judge0.language-id}") Integer languageId,
            @Value("${judge0.timeout}") Integer timeout,
            @Value("${judge0.max-poll-attempts}") Integer maxPollAttempts,
            @Value("${judge0.poll-interval}") Integer pollInterval
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.languageId = languageId;
        this.timeout = timeout;
        this.maxPollAttempts = maxPollAttempts;
        this.pollInterval = pollInterval;
    }

    /**
     * Submit code to Judge0 for execution
     */
    public String submitCode(String sourceCode, String stdin) {
        log.info("Submitting code to Judge0");

        Judge0SubmissionRequest request = Judge0SubmissionRequest.builder()
                .sourceCode(sourceCode)
                .languageId(languageId)
                .stdin(stdin != null ? stdin : "")
                .expectedOutput(null)
                .build();

        Judge0SubmissionResponse response = webClient.post()
                .uri("/submissions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Judge0SubmissionResponse.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
                .block(Duration.ofMillis(timeout));

        if (response == null || response.getToken() == null) {
            throw new RuntimeException("Failed to submit code to Judge0");
        }

        log.info("Code submitted successfully, token: {}", response.getToken());
        return response.getToken();
    }

    /**
     * Get submission result by token
     */
    public Judge0ResultResponse getSubmissionResult(String token) {
        log.debug("Fetching result for token: {}", token);

        return webClient.get()
                .uri("/submissions/{token}?base64_encoded=false", token)
                .retrieve()
                .bodyToMono(Judge0ResultResponse.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
                .block(Duration.ofMillis(timeout));
    }

    /**
     * Submit code and wait for execution to complete
     */
    public Judge0ResultResponse executeAndWait(String sourceCode, String stdin) {
        String token = submitCode(sourceCode, stdin);
        return pollForResult(token);
    }

    /**
     * Poll Judge0 for result until execution completes or timeout
     */
    private Judge0ResultResponse pollForResult(String token) {
        log.info("Polling for result, token: {}", token);

        for (int attempt = 0; attempt < maxPollAttempts; attempt++) {
            Judge0ResultResponse result = getSubmissionResult(token);

            if (result == null) {
                throw new RuntimeException("Failed to get submission result from Judge0");
            }

            Integer statusId = result.getStatus().getId();

            // Status 1 = In Queue, Status 2 = Processing
            if (statusId != 1 && statusId != 2) {
                log.info("Execution completed with status: {}", result.getStatus().getDescription());
                return result;
            }

            log.debug("Execution still in progress (attempt {}/{}), status: {}",
                    attempt + 1, maxPollAttempts, result.getStatus().getDescription());

            try {
                Thread.sleep(pollInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Polling interrupted", e);
            }
        }

        throw new RuntimeException("Execution timeout: max poll attempts exceeded");
    }
}
