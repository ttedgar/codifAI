package com.edi.backend.exception;

public class ChallengeNotFoundException extends RuntimeException {
    public ChallengeNotFoundException(Long id) {
        super("Challenge not found with id: " + id);
    }

    public ChallengeNotFoundException(String message) {
        super(message);
    }
}
