package com.edi.backend.controller;

import com.edi.backend.dto.ChallengeRequest;
import com.edi.backend.dto.ChallengeResponse;
import com.edi.backend.dto.PageResponse;
import com.edi.backend.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
@Tag(name = "Challenges", description = "Challenge management endpoints")
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping
    @Operation(summary = "Get all challenges", description = "Returns paginated list of all challenges")
    public ResponseEntity<PageResponse<ChallengeResponse>> getAllChallenges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        return ResponseEntity.ok(challengeService.getAllChallenges(page, size, sortBy));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get challenge by ID", description = "Returns a single challenge by its ID")
    public ResponseEntity<ChallengeResponse> getChallengeById(@PathVariable Long id) {
        return ResponseEntity.ok(challengeService.getChallengeById(id));
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new challenge", description = "Triggers AI to generate a new challenge (requires authentication)")
    public ResponseEntity<ChallengeResponse> createChallenge(@Valid @RequestBody ChallengeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(challengeService.createChallenge(request));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete challenge", description = "Deletes a challenge (requires ADMIN role)")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        challengeService.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }
}
