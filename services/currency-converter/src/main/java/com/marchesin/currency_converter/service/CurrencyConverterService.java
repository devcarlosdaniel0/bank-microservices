package com.marchesin.currency_converter.service;

import com.marchesin.currency_converter.dto.CurrencyData;
import com.marchesin.currency_converter.dto.CurrencyResponse;
import com.marchesin.currency_converter.exception.InsufficientAmountValueException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class CurrencyConverterService {
    private final CurrencyApiService currencyApiService;

    public CurrencyConverterService(CurrencyApiService currencyApiService) {
        this.currencyApiService = currencyApiService;
    }

    public CurrencyResponse convert(String from, String to, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientAmountValueException("Amount value must be greater than zero");
        }

        String symbols = (from + "_" + to).toUpperCase();

        CurrencyData data = currencyApiService.getCurrencyData(symbols);

        BigDecimal exchangeRate = data.price();
        BigDecimal convertedAmount = exchangeRate.multiply(amount);

        return new CurrencyResponse(
                symbols,
                round(exchangeRate),
                amount,
                round(convertedAmount),
                getTimestampFormatted(data)
        );
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_EVEN);
    }

    private LocalDateTime getTimestampFormatted(CurrencyData currencyData) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(currencyData.timestamp()),
                ZoneOffset.UTC);
    }
}
