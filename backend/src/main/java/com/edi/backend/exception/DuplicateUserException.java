package com.edi.backend.exception;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String field, String value) {
        super(field.substring(0, 1).toUpperCase() + field.substring(1) + " '" + value + "' already exists");
    }

    public DuplicateUserException(String message) {
        super(message);
    }
}
