package com.marchesin.account.exception;

public class InsufficientFunds extends RuntimeException {
    public InsufficientFunds(String message) {
        super(message);
    }
}
