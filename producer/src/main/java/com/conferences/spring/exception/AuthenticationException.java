package com.conferences.spring.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException() {
        super("Incorrect login or password entered");
    }
}
