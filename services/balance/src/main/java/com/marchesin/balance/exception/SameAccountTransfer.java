package com.marchesin.balance.exception;

public class SameAccountTransfer extends RuntimeException {
    public SameAccountTransfer(String message) {
        super(message);
    }
}
