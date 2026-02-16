package com.edi.backend.service;

import com.edi.backend.dto.Judge0ResultResponse;
import com.edi.backend.dto.Judge0SubmissionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Base64;

@Service
@Slf4j
public class Judge0Service {

    private final WebClient webClient;
    private final Integer languageId;
    private final Integer timeout;

    public Judge0Service(
            @Value("${judge0.base-url}") String baseUrl,
            @Value("${judge0.language-id}") Integer languageId,
            @Value("${judge0.timeout}") Integer timeout
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.languageId = languageId;
        this.timeout = timeout;
    }

    /**
     * Submit code and wait for execution to complete (synchronous)
     */
    public Judge0ResultResponse executeAndWait(String sourceCode, String stdin) {
        log.info("Submitting code to Judge0 with synchronous execution");

        // Base64 encode the source code and stdin
        String encodedSourceCode = Base64.getEncoder().encodeToString(sourceCode.getBytes());
        String encodedStdin = Base64.getEncoder().encodeToString((stdin != null ? stdin : "").getBytes());

        Judge0SubmissionRequest request = Judge0SubmissionRequest.builder()
                .sourceCode(encodedSourceCode)
                .languageId(languageId)
                .stdin(encodedStdin)
                .expectedOutput(null)
                .build();

        // Use synchronous execution with wait=true and base64_encoded=true
        Judge0ResultResponse response = webClient.post()
                .uri("/submissions?base64_encoded=true&wait=true")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Judge0ResultResponse.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
                .block(Duration.ofMillis(timeout));

        if (response == null) {
            throw new RuntimeException("Failed to execute code via Judge0");
        }

        // Decode base64-encoded outputs
        decodeBase64Fields(response);

        log.info("Execution completed with status: {}", response.getStatus().getDescription());
        return response;
    }

    /**
     * Decode base64-encoded fields in Judge0 response
     */
    private void decodeBase64Fields(Judge0ResultResponse response) {
        if (response.getStdout() != null && !response.getStdout().isEmpty()) {
            try {
                // Remove newlines from base64 string before decoding
                String cleanedStdout = response.getStdout().replaceAll("\\s", "");
                response.setStdout(new String(Base64.getDecoder().decode(cleanedStdout)));
            } catch (IllegalArgumentException e) {
                log.warn("Failed to decode stdout, keeping original value", e);
            }
        }

        if (response.getStderr() != null && !response.getStderr().isEmpty()) {
            try {
                // Remove newlines from base64 string before decoding
                String cleanedStderr = response.getStderr().replaceAll("\\s", "");
                response.setStderr(new String(Base64.getDecoder().decode(cleanedStderr)));
            } catch (IllegalArgumentException e) {
                log.warn("Failed to decode stderr, keeping original value", e);
            }
        }

        if (response.getCompileOutput() != null && !response.getCompileOutput().isEmpty()) {
            try {
                // Remove newlines from base64 string before decoding
                String cleanedCompileOutput = response.getCompileOutput().replaceAll("\\s", "");
                response.setCompileOutput(new String(Base64.getDecoder().decode(cleanedCompileOutput)));
            } catch (IllegalArgumentException e) {
                log.warn("Failed to decode compile_output, keeping original value", e);
            }
        }
    }
}
