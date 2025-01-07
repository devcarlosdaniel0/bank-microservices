package com.project.currency_converter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidSyntaxException extends RuntimeException {
    public InvalidSyntaxException(String message) {
        super(message);
    }
}
