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

    public CurrencyResponse convertCurrencies(String symbols) {
        Map<String, CurrencyData> response = currencyConverterClient.getCurrencyConversion(symbols, tokenAPI);

        CurrencyData data = response.get(symbols);

        LocalDateTime timestampFormatted = LocalDateTime.ofInstant(Instant.ofEpochSecond(data.timestamp()), ZoneOffset.UTC);
        BigDecimal priceRounded = BigDecimal.valueOf(data.price()).setScale(2, RoundingMode.HALF_EVEN);

        return CurrencyResponse.builder()
                .symbols(symbols)
                .price(priceRounded)
                .timestamp(timestampFormatted)
                .build();
    }
}
