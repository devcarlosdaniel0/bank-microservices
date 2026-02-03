package com.marchesin.currency_converter.service;

import com.marchesin.currency_converter.client.InvertextoClient;
import com.marchesin.currency_converter.domain.CurrencyProvider;
import com.marchesin.currency_converter.dto.CurrencyResponse;
import com.marchesin.currency_converter.dto.invertexto.InvertextoData;
import com.marchesin.currency_converter.exception.CurrencyNotFoundException;
import com.marchesin.currency_converter.utils.CurrencyUtils;
import com.marchesin.currency_converter.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@Qualifier("invertexto")
public class InvertextoImpl implements CurrencyProvider {
    private final InvertextoClient client;

    public InvertextoImpl(InvertextoClient client) {
        this.client = client;
    }

    @Override
    public CurrencyResponse convert(String from, String to, BigDecimal amount) {
        String symbols = CurrencyUtils.symbols(from, to);

        Map<String, InvertextoData> response = client.getCurrencyConversion(symbols);

        InvertextoData data = response.get(symbols);

        if (data == null) {
            throw new CurrencyNotFoundException("Currency symbol not found in response: " + symbols);
        }

        BigDecimal exchangeRate = data.price();
        BigDecimal convertedAmount = exchangeRate.multiply(amount).setScale(2, RoundingMode.HALF_EVEN);

        return new CurrencyResponse(
                symbols,
                exchangeRate.setScale(6, RoundingMode.HALF_EVEN),
                amount.setScale(2, RoundingMode.HALF_EVEN),
                convertedAmount.setScale(2, RoundingMode.HALF_EVEN),
                TimeUtils.getTimestampFormatted(data.timestamp())
        );
    }
}
