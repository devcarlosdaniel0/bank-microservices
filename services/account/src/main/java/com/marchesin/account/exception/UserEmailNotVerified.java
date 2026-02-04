package com.marchesin.account.exception;

import org.springframework.http.HttpStatus;

public class UserEmailNotVerified extends BusinessException {
    public UserEmailNotVerified(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
