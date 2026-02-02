package com.marchesin.account.service;

import com.marchesin.account.client.CurrencyConverterFeignClient;
import com.marchesin.account.domain.CurrencyCode;
import com.marchesin.account.domain.CurrencyConverter;
import com.marchesin.account.dto.CurrencyResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CurrencyConverterImpl implements CurrencyConverter {
    private final CurrencyConverterFeignClient client;

    public CurrencyConverterImpl(CurrencyConverterFeignClient client) {
        this.client = client;
    }

    @Override
    public BigDecimal convert(CurrencyCode from, CurrencyCode to, BigDecimal amount) {
        CurrencyResponse response = client.convert(from.getValue(), to.getValue(), amount);

        return response.convertedAmount();
    }
}
