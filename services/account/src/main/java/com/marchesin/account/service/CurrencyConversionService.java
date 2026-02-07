package com.marchesin.account.service;

import com.marchesin.account.client.CurrencyConverterFeignClient;
import com.marchesin.account.domain.CurrencyCode;
import com.marchesin.account.dto.external.CurrencyResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CurrencyConversionService {

    private final CurrencyConverterFeignClient client;

    public CurrencyConversionService(CurrencyConverterFeignClient client) {
        this.client = client;
    }

    public BigDecimal convert(CurrencyCode from, CurrencyCode to, BigDecimal amount) {
        if (from.equals(to)) {
            return amount;
        }

        CurrencyResponse response = client.convert(from.getValue(), to.getValue(), amount);

        return response.convertedAmount();
    }
}