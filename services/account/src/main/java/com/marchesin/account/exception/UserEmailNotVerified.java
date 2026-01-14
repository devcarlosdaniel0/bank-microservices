package com.marchesin.account.exception;

public class UserEmailNotVerified extends RuntimeException {
    public UserEmailNotVerified(String message) {
        super(message);
    }
}
