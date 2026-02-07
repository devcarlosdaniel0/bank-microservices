package com.marchesin.currency_converter.service;

import com.marchesin.currency_converter.domain.CurrencyProvider;
import com.marchesin.currency_converter.dto.CurrencyResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CurrencyConverterService {
    private final CurrencyProvider currencyProvider;

    public CurrencyConverterService(CurrencyProvider currencyProvider) {
        this.currencyProvider = currencyProvider;
    }

    public CurrencyResponse convert(String from, String to, BigDecimal amount) {
        return currencyProvider.convert(from, to, amount);
    }

}
