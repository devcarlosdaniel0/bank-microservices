package com.marchesin.currency_converter.service;

import com.marchesin.currency_converter.client.InvertextoClient;
import com.marchesin.currency_converter.domain.CurrencyProvider;
import com.marchesin.currency_converter.dto.CurrencyResponse;
import com.marchesin.currency_converter.dto.invertexto.InvertextoData;
import com.marchesin.currency_converter.exception.CustomFeignException;
import com.marchesin.currency_converter.utils.CurrencyUtils;
import feign.FeignException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@ConditionalOnProperty(
        name = "currency.provider",
        havingValue = "invertexto"
)
public class InvertextoImpl implements CurrencyProvider {
    private final InvertextoClient client;
    private final CurrencyResponseFactory factory;

    public InvertextoImpl(InvertextoClient client, CurrencyResponseFactory factory) {
        this.client = client;
        this.factory = factory;
    }

    @Override
    public CurrencyResponse convert(String from, String to, BigDecimal amount) {
        String symbols = CurrencyUtils.symbols(from, to);

        Map<String, InvertextoData> response;

        try {
            response = client.convert(symbols);
        } catch (FeignException e) {
            HttpStatus status = HttpStatus.resolve(e.status());

            throw new CustomFeignException(
                    status != null ? status : HttpStatus.BAD_GATEWAY,
                    "External Invertexto API error"
            );
        }

        InvertextoData data = response.get(symbols);

        BigDecimal exchangeRate = data.price();
        BigDecimal convertedAmount = exchangeRate.multiply(amount);

        return factory.buildResponse(symbols, exchangeRate, amount, convertedAmount, data.timestamp());
    }
}
