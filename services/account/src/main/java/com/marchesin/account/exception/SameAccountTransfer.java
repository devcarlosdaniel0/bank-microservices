package com.marchesin.account.exception;

import org.springframework.http.HttpStatus;

public class SameAccountTransfer extends BusinessException {
    public SameAccountTransfer(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
