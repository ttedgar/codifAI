package com.edi.backend.exception;

public class ChallengeGenerationException extends RuntimeException {
    public ChallengeGenerationException(String message) {
        super(message);
    }

    public ChallengeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
