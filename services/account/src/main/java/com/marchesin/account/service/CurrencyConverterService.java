package com.marchesin.account.service;

import com.marchesin.account.client.CurrencyConverterFeignClient;
import com.marchesin.account.domain.CurrencyCode;
import com.marchesin.account.dto.external.CurrencyResponse;
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

    public BigDecimal convert(CurrencyCode from, CurrencyCode to, BigDecimal amount) {
        if (from.equals(to)) {
            return amount;
        }

        CurrencyResponse response;

        try {
            response = client.convert(from.getValue(), to.getValue(), amount);
        } catch (FeignException e) {
            throw new CurrencyConverterUnavailable("Currency converter unavailable");
        }

        return response.convertedAmount();
    }
}