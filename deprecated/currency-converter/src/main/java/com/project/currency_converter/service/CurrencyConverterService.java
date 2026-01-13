package com.project.currency_converter.service;

import com.project.currency_converter.dto.CurrencyData;
import com.project.currency_converter.dto.CurrencyResponse;
import com.project.currency_converter.exception.InsufficientAmountValueException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class CurrencyConverterService {
    private final CurrencyApiService currencyApiService;

    public CurrencyResponse convertCurrencies(BigDecimal amount, String symbols) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientAmountValueException("Amount value must be greater than zero");
        }

        CurrencyData currencyData = currencyApiService.getCurrencyData(symbols).get(symbols);

        BigDecimal exchangeRate = BigDecimal.valueOf(currencyData.price());
        BigDecimal convertedAmount = exchangeRate.multiply(amount);

        return buildCurrencyResponse(amount, symbols, exchangeRate, convertedAmount, currencyData);
    }

    private CurrencyResponse buildCurrencyResponse(BigDecimal amount, String symbols, BigDecimal exchangeRate, BigDecimal convertedAmount, CurrencyData currencyData) {
        return CurrencyResponse.builder()
                .amount(amount)
                .symbols(symbols)
                .exchangeRate(getRoundedPrice(exchangeRate))
                .convertedAmount(getRoundedPrice(convertedAmount))
                .timestamp(getTimestampFormatted(currencyData))
                .build();
    }

    private LocalDateTime getTimestampFormatted(CurrencyData currencyData) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(currencyData.timestamp()),
                ZoneOffset.UTC);
    }

    private BigDecimal getRoundedPrice(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_EVEN);
    }
}
