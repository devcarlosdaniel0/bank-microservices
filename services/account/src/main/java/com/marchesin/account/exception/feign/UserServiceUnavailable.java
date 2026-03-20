package com.marchesin.account.exception.feign;

import com.marchesin.account.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserServiceUnavailable extends BusinessException {
    public UserServiceUnavailable(String detail) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, detail);
    }
}
