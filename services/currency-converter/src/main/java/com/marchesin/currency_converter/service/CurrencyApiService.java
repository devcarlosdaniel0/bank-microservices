package com.marchesin.currency_converter.service;


import com.marchesin.currency_converter.client.InvertextoCurrencyClient;
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
    private final InvertextoCurrencyClient client;

    public CurrencyApiService(InvertextoCurrencyClient client) {
        this.client = client;
    }

    @Value("${token.api.invertexto}")
    private String tokenAPI;

    public CurrencyData getCurrencyData(String symbols) {
        try {
            Map<String, CurrencyData> response = client.getCurrencyConversion(symbols, tokenAPI);

            if (response.get(symbols) == null) {
                throw new CurrencyNotFoundException("Currency symbol not found in response: " + symbols);
            }

            return response.get(symbols);
        } catch (FeignException.NotFound e) {
            throw new CurrencyNotFoundException("Currency symbols not found: " + symbols);
        } catch (FeignException.UnprocessableEntity e) {
            throw new InvalidSyntaxException("The symbols must contain currencies ISO between underscore");
        } catch (FeignException e) {
            if (e.status() == -1) {
                throw new TimeoutException("Timeout occurred while calling the external API");
            }

            String statusReason = HttpStatus.valueOf(e.status()).getReasonPhrase();
            throw new ExternalApiException(String.format("Status code: %d [%s]", e.status(), statusReason));
        }
    }

}
