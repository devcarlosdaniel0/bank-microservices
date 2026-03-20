package com.marchesin.account.service.external;

import com.marchesin.account.client.CurrencyConverterFeignClient;
import com.marchesin.account.domain.CurrencyCode;
import com.marchesin.account.dto.external.CurrencyResponse;
import com.marchesin.account.exception.SameCurrencyException;
import com.marchesin.account.exception.feign.CurrencyConverterUnavailable;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CurrencyConverterService {

    private final CurrencyConverterFeignClient client;

    public CurrencyConverterService(CurrencyConverterFeignClient client) {
        this.client = client;
    }

    public CurrencyResponse convert(CurrencyCode currencyFrom, CurrencyCode currencyTo, BigDecimal amount) {
        if (currencyFrom.equals(currencyTo)) {
            throw new SameCurrencyException("Can not convert same currencies");
        }

        try {
            return client.convert(currencyFrom.getValue(), currencyTo.getValue(), amount);
        } catch (FeignException e) {
            throw new CurrencyConverterUnavailable("Currency converter unavailable");
        }
    }
}