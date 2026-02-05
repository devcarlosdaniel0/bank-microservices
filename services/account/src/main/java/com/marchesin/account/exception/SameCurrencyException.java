package com.marchesin.account.exception;

import org.springframework.http.HttpStatus;

public class SameCurrencyException extends BusinessException {
    public SameCurrencyException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
