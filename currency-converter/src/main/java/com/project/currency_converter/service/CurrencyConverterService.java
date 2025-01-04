package com.project.currency_converter.service;

import com.project.currency_converter.client.CurrencyConverterClient;
import com.project.currency_converter.dto.CurrencyData;
import com.project.currency_converter.dto.CurrencyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyConverterService {
    private final CurrencyConverterClient currencyConverterClient;

    @Value("${token.api.invertexto}")
    private String tokenAPI;

    public CurrencyResponse convertCurrencies(BigDecimal amount, String symbols) {
        Map<String, CurrencyData> response = currencyConverterClient.getCurrencyConversion(symbols, tokenAPI);

        CurrencyData currencyData = response.get(symbols);

        BigDecimal exchangeRate = BigDecimal.valueOf(currencyData.price());

        BigDecimal convertedAmount = exchangeRate.multiply(amount);

        return CurrencyResponse.builder()
                .symbols(symbols)
                .exchangeRate(getRoundedPrice(exchangeRate))
                .amount(amount)
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
