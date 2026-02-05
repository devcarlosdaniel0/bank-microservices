package com.marchesin.account.exception;

import org.springframework.http.HttpStatus;

public class InvalidCurrencyCode extends BusinessException {
    public InvalidCurrencyCode(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
