package com.marchesin.currency_converter.service;


import com.marchesin.currency_converter.client.CurrencyConverterClient;
import com.marchesin.currency_converter.dto.CurrencyData;
import com.marchesin.currency_converter.exception.CurrencyNotFoundException;
import com.marchesin.currency_converter.exception.ExternalApiException;
import com.marchesin.currency_converter.exception.InvalidSyntaxException;
import com.marchesin.currency_converter.exception.TimeoutException;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CurrencyApiService {
    private final CurrencyConverterClient currencyConverterClient;

    public CurrencyApiService(CurrencyConverterClient currencyConverterClient) {
        this.currencyConverterClient = currencyConverterClient;
    }

    @Value("${token.api.invertexto}")
    private String tokenAPI;

    public Map<String, CurrencyData> getCurrencyData(String symbols) {
        try {
            return currencyConverterClient.getCurrencyConversion(symbols, tokenAPI);
        } catch (FeignException.NotFound e) {
            throw new CurrencyNotFoundException(String.format("Currencies symbols not found: '%s'",  symbols));
        } catch (FeignException.UnprocessableEntity e) {
            throw new InvalidSyntaxException("Example: USD_EUR");
        } catch (FeignException e) {
            int statusCode = e.status();

            if (statusCode == -1) {
                throw new TimeoutException("Timeout occurred while calling the external API");
            }

            String statusReason = HttpStatus.valueOf(statusCode).getReasonPhrase();
            throw new ExternalApiException(String.format("Status code: %d [%s]", statusCode, statusReason));
        }
    }

}
