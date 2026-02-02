package com.marchesin.account.exception;

public class InvalidCurrencyCode extends RuntimeException {
    public InvalidCurrencyCode(String message) {
        super(message);
    }
}
