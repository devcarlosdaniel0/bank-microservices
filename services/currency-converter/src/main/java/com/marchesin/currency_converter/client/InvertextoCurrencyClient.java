package com.marchesin.currency_converter.client;

import com.marchesin.currency_converter.config.FeignConfig;
import com.marchesin.currency_converter.dto.CurrencyData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "invertexto-api",
        url = "https://api.invertexto.com/v1/currency",
        configuration = FeignConfig.class)
public interface InvertextoCurrencyClient {

    @GetMapping("/{symbols}")
    Map<String, CurrencyData> getCurrencyConversion(
            @PathVariable("symbols") String symbols,
            @RequestParam("token") String token
    );
}