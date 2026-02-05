package com.marchesin.account.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyHasAccount extends BusinessException {
    public UserAlreadyHasAccount(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
