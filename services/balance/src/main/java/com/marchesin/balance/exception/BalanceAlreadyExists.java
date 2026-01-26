package com.marchesin.balance.exception;

public class BalanceAlreadyExists extends RuntimeException {
    public BalanceAlreadyExists(String message) {
        super(message);
    }
}
