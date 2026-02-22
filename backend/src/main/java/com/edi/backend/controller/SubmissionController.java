package com.edi.backend.controller;

import com.edi.backend.dto.SubmissionRequest;
import com.edi.backend.dto.SubmissionResponse;
import com.edi.backend.entity.User;
import com.edi.backend.exception.UserNotFoundException;
import com.edi.backend.repository.UserRepository;
import com.edi.backend.service.CodeExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
@Tag(name = "Submissions", description = "Code submission and execution endpoints")
public class SubmissionController {

    private final CodeExecutionService codeExecutionService;
    private final UserRepository userRepository;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Submit code for evaluation", description = "Submit code solution for a challenge (requires authentication)")
    public ResponseEntity<SubmissionResponse> submitCode(@Valid @RequestBody SubmissionRequest request) {
        // Get authenticated user
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Evaluate submission
        SubmissionResponse response = codeExecutionService.evaluateSubmission(
                user.getId(),
                request.getChallengeId(),
                request.getCode()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
