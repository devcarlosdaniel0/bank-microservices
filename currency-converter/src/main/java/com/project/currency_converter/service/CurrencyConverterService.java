package com.project.currency_converter.service;

import com.project.currency_converter.client.CurrencyConverterClient;
import com.project.currency_converter.dto.CurrencyData;
import com.project.currency_converter.dto.CurrencyResponse;
import com.project.currency_converter.exception.CurrencyNotFoundException;
import com.project.currency_converter.exception.ExternalApiException;
import com.project.currency_converter.exception.InsufficientAmountValueException;
import com.project.currency_converter.exception.InvalidSyntaxException;
import feign.Feign;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientAmountValueException("Amount value must be greater than zero");
        }

        Map<String, CurrencyData> response;

        try {
            response = currencyConverterClient.getCurrencyConversion(symbols, tokenAPI);
        } catch (FeignException.NotFound e) {
            throw new CurrencyNotFoundException(String.format("Currencies symbols not found: '%s'",  symbols));
        } catch (FeignException.UnprocessableEntity e) {
            throw new InvalidSyntaxException("Example: USD_EUR");
        } catch (FeignException e) {
            int statusCode = e.status();
            String statusReason = HttpStatus.valueOf(statusCode).getReasonPhrase();
            throw new ExternalApiException(String.format("Status code: %d [%s]", statusCode, statusReason));
        }

        CurrencyData currencyData = response.get(symbols);

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
