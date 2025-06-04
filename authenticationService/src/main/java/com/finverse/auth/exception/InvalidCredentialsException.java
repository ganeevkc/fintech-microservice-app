package com.finverse.auth.exception; // Adjust package as needed

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}