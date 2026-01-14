package com.marchesin.account.exception;

public class UserAlreadyHasAccount extends RuntimeException {
    public UserAlreadyHasAccount(String message) {
        super(message);
    }
}
