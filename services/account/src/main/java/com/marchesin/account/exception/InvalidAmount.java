package com.marchesin.account.exception;

public class InvalidAmount extends RuntimeException {
    public InvalidAmount(String message) {
        super(message);
    }
}
