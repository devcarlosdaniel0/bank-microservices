package com.project.auth.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ConfirmationTokenExpiredException extends RuntimeException {
    public ConfirmationTokenExpiredException(String message) {
        super(message);
    }
}
