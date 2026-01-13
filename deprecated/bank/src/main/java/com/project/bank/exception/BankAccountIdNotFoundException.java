package com.project.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BankAccountIdNotFoundException extends RuntimeException {
    public BankAccountIdNotFoundException(String message) {
        super(message);
    }
}
