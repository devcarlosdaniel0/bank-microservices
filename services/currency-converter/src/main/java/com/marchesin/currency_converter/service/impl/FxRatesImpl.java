package com.marchesin.currency_converter.service.impl;

import com.marchesin.currency_converter.client.FxRatesClient;
import com.marchesin.currency_converter.dto.CurrencyResponse;
import com.marchesin.currency_converter.dto.fxrates.FxConvertResponse;
import com.marchesin.currency_converter.exception.CustomFeignException;
import com.marchesin.currency_converter.service.CurrencyProvider;
import com.marchesin.currency_converter.service.CurrencyResponseFactory;
import com.marchesin.currency_converter.utils.CurrencyUtils;
import feign.FeignException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@ConditionalOnProperty(
        name = "currency.provider",
        havingValue = "fxRates",
        matchIfMissing = true
)
public class FxRatesImpl implements CurrencyProvider {

    private final FxRatesClient client;
    private final CurrencyResponseFactory factory;

    public FxRatesImpl(FxRatesClient client, CurrencyResponseFactory factory) {
        this.client = client;
        this.factory = factory;
    }

    @Override
    public CurrencyResponse convert(String from, String to, BigDecimal amount) {
        FxConvertResponse response;

        try {
            response = client.convert(CurrencyUtils.normalize(from), CurrencyUtils.normalize(to), amount);
        } catch (FeignException e) {
            HttpStatus status = HttpStatus.resolve(e.status());

            throw new CustomFeignException(
                    status != null ? status : HttpStatus.BAD_GATEWAY,
                    "External FxRates API error"
            );
        }

        String symbols = CurrencyUtils.symbols(response.query().from(), response.query().to());
        BigDecimal exchangeRate = response.info().rate();
        BigDecimal convertedAmount = response.result();

        return factory.buildResponse(symbols, exchangeRate, amount, convertedAmount, response.timestamp());
    }
}
