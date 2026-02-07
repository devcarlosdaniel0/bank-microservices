package com.marchesin.account.exception.feign;

import com.marchesin.account.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserNotFound extends BusinessException {
    public UserNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
