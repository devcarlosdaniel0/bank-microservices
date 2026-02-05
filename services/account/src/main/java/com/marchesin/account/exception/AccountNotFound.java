package com.marchesin.account.exception;

import org.springframework.http.HttpStatus;

public class AccountNotFound extends BusinessException {
    public AccountNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
