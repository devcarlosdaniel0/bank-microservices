package com.marchesin.account.exception.feign;

import com.marchesin.account.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class CurrencyConverterUnavailable extends BusinessException {
    public CurrencyConverterUnavailable(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

}
