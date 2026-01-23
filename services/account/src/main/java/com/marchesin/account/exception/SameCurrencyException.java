package com.marchesin.account.exception;

public class SameCurrencyException extends RuntimeException {
    public SameCurrencyException(String message) {
        super(message);
    }
}
