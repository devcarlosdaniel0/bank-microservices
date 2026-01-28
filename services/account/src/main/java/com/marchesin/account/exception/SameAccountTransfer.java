package com.marchesin.account.exception;

public class SameAccountTransfer extends RuntimeException {
    public SameAccountTransfer(String message) {
        super(message);
    }
}
