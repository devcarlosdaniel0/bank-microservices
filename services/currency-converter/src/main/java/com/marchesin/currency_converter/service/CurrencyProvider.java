package com.marchesin.currency_converter.service;

import com.marchesin.currency_converter.dto.CurrencyResponse;

import java.math.BigDecimal;

public interface CurrencyProvider {

    CurrencyResponse convert(String from, String to, BigDecimal amount);
}