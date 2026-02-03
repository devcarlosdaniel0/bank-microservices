package com.marchesin.currency_converter.service;

import com.marchesin.currency_converter.client.FxRatesClient;
import com.marchesin.currency_converter.domain.CurrencyProvider;
import com.marchesin.currency_converter.dto.CurrencyResponse;
import com.marchesin.currency_converter.dto.fxrates.FxConvertResponse;
import com.marchesin.currency_converter.utils.CurrencyUtils;
import com.marchesin.currency_converter.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Qualifier("fxRates")
public class FxRatesImpl implements CurrencyProvider {

    private final FxRatesClient client;

    public FxRatesImpl(FxRatesClient client) {
        this.client = client;
    }

    @Override
    public CurrencyResponse convert(String from, String to, BigDecimal amount) {
        FxConvertResponse response = client.convert(
                CurrencyUtils.normalize(from), CurrencyUtils.normalize(to), amount);

        if (!response.success()) {
            throw new RuntimeException("Fail to convert");
        }

        return new CurrencyResponse(
                CurrencyUtils.symbols(from, to),
                response.info().rate().setScale(6, RoundingMode.HALF_EVEN),
                response.query().amount().setScale(2, RoundingMode.HALF_EVEN),
                response.result().setScale(2, RoundingMode.HALF_EVEN),
                TimeUtils.getTimestampFormatted(response.timestamp())
        );
    }
}
