package com.marchesin.currency_converter.exception;

import org.springframework.http.HttpStatus;

public class CustomFeignException extends ApiException {
    public CustomFeignException(HttpStatus status, String detail) {
        super(status, detail);
    }
}
