package com.marchesin.currency_converter.exception;

public class InsufficientAmountValueException extends RuntimeException {
    public InsufficientAmountValueException(String message) {
        super(message);
    }
}
