package com.marchesin.account.exception;

import org.springframework.http.HttpStatus;

public class InsufficientFunds extends BusinessException {
    public InsufficientFunds(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
