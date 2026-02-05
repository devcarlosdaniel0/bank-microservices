package com.marchesin.account.exception;

import org.springframework.http.HttpStatus;

public class InvalidAmount extends BusinessException {
    public InvalidAmount(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
