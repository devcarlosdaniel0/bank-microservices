package com.project.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyHasBankAccountException extends RuntimeException {
    public UserAlreadyHasBankAccountException(String message) {
        super(message);
    }
}
