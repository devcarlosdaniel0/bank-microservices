package com.project.auth.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountAlreadyConfirmedException extends RuntimeException {
    public AccountAlreadyConfirmedException(String message) {
        super(message);
    }
}
