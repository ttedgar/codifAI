package com.edi.backend.service;

import com.edi.backend.dto.ChallengeGenerationRequest;
import com.edi.backend.entity.Difficulty;
import com.edi.backend.exception.ChallengeGenerationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService implements AiChallengeGenerator {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key}")
    private String apiKey;

    public ChallengeGenerationRequest generateChallenge(String prompt, Difficulty difficulty, List<String> existingTitles) {
        try {
            String systemPrompt = buildSystemPrompt(existingTitles);
            Map<String, Object> payload = buildPayload(systemPrompt, prompt, difficulty);

            JsonNode response = webClient
                    .post()
                    .uri("/v1beta/models/gemini-2.5-flash:generateContent?key={apiKey}", apiKey)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response == null) {
                throw new ChallengeGenerationException("Gemini API returned null response");
            }

            String generatedText = response
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

            if (generatedText.isEmpty()) {
                throw new ChallengeGenerationException("Gemini API returned empty text content");
            }

            generatedText = generatedText.trim();
            if (generatedText.startsWith("```json")) {
                generatedText = generatedText.substring(7);
            } else if (generatedText.startsWith("```")) {
                generatedText = generatedText.substring(3);
            }
            if (generatedText.endsWith("```")) {
                generatedText = generatedText.substring(0, generatedText.length() - 3);
            }
            generatedText = generatedText.trim();

            log.info("Generated text: " + generatedText);
            return parseGeneratedChallenge(generatedText);
        } catch (ChallengeGenerationException e) {
            log.error("Challenge generation error", e);
            throw e;
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            throw new ChallengeGenerationException("Failed to generate challenge: " + e.getMessage(), e);
        }
    }

    private String buildSystemPrompt(List<String> existingTitles) {
      return "You are an expert coding challenge generator. Generate a single JSON object (no markdown, no extra text) with these exact fields:\n" +
          "{\n" +
          "  \"title\": \"Brief challenge title (50 chars max)\",\n" +
          "  \"description\": \"Detailed problem statement (500 chars max)\",\n" +
          "  \"starterCode\": \"JavaScript function template ONLY. Must be in this exact format with function name, parameters, and a comment placeholder. Example: function filterHeroesByPower(heroes, requiredPower) { // Your code here }\",\n" +
          "  \"hiddenTests\": \"Hidden unit tests in JavaScript. Each test MUST output exactly 'PASS' or 'FAIL [message]' to stdout using console.log. Use try-catch to wrap test logic and output FAIL on error.\",\n" +
          "  \"sampleTests\": \"Sample test cases visible to the user\",\n" +
          "  \"tags\": [\"tag1\", \"tag2\"] - relevant programming concepts\n" +
          "}\n\n" +
          "CRITICAL starterCode format:\n" +
          "function functionName(param1, param2) {\n" +
          "  // Your code here\n" +
          "}\n" +
          "Do NOT include function body implementation. Do NOT include docstrings. Just the signature and comment.\n\n" +
          "CRITICAL: Test Results MUST be Objectively Examinable:\n" +
          "- Tests must verify EXACT, VERIFIABLE results (not subjective sentences or text)\n" +
          "- Use strict equality checks with numbers, booleans, arrays, or objects\n" +
          "- NEVER expect sentence strings that could be worded differently\n" +
          "- NEVER use loose comparisons or substring checks\n" +
          "- ONLY use === strict equality\n\n" +
          "CRITICAL: Hidden tests MUST output results as follows:\n" +
          "- Each test wrapped in try-catch\n" +
          "- On success: console.log('PASS')\n" +
          "- On failure: console.log('FAIL: [message]')\n" +
          "Example test structure:\n" +
          "try {\n" +
          "  const result = myFunction(input);\n" +
          "  if (result === expected) {\n" +
          "    console.log('PASS');\n" +
          "  } else {\n" +
          "    console.log('FAIL: Expected X, got Y');\n" +
          "  }\n" +
          "} catch (e) {\n" +
          "  console.log('FAIL: ' + e.message);\n" +
          "}\n\n" +
          "Only generate JavaScript challenges. Ensure starter code is valid JavaScript.\n" +
          "Avoid these titles: " + existingTitles + "\n" +
          "Return ONLY the JSON object, nothing else.";
    }

    private Map<String, Object> buildPayload(String systemPrompt, String userPrompt, Difficulty difficulty) {
        String fullPrompt = systemPrompt + "\n\nUser Request (Difficulty: " + difficulty + "): " + userPrompt;

        return Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of("text", fullPrompt)
                                )
                        )
                )
        );
    }

    private ChallengeGenerationRequest parseGeneratedChallenge(String jsonText) {
        try {
            JsonNode node = objectMapper.readTree(jsonText);
            return ChallengeGenerationRequest.builder()
                    .title(node.path("title").asText())
                    .description(node.path("description").asText())
                    .starterCode(node.path("starterCode").asText())
                    .hiddenTests(node.path("hiddenTests").asText())
                    .sampleTests(node.path("sampleTests").asText())
                    .tags(objectMapper.convertValue(node.path("tags"), List.class))
                    .build();
        } catch (Exception e) {
            log.error("Error parsing generated challenge JSON", e);
            throw new ChallengeGenerationException("Failed to parse generated challenge: " + e.getMessage(), e);
        }
    }
}
