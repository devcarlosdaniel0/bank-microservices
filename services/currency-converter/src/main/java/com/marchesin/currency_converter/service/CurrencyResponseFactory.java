package com.marchesin.currency_converter.service;

import com.marchesin.currency_converter.dto.CurrencyResponse;
import com.marchesin.currency_converter.utils.TimeUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CurrencyResponseFactory {

    public CurrencyResponse buildResponse(String symbols, BigDecimal exchangeRate, BigDecimal amount, BigDecimal convertedAmount, Long timestamp) {
        return new CurrencyResponse(
                symbols,
                exchangeRate.setScale(6, RoundingMode.HALF_EVEN),
                amount.setScale(2, RoundingMode.HALF_EVEN),
                convertedAmount.setScale(2, RoundingMode.HALF_EVEN),
                TimeUtils.getTimestampFormatted(timestamp)
        );
    }
}
