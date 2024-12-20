package com.project.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnconfirmedUserException extends RuntimeException {
    public UnconfirmedUserException(String message) {
        super(message);
    }
}
