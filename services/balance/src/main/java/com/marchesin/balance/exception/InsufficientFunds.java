package com.marchesin.balance.exception;

public class InsufficientFunds extends RuntimeException {
    public InsufficientFunds(String message) {
        super(message);
    }
}
