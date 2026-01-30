package com.marchesin.currency_converter.exception;

public class TimeoutException extends RuntimeException {
    public TimeoutException(String message) {
        super(message);
    }
}