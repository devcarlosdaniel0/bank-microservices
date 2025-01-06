package com.project.currency_converter.client;

import com.project.currency_converter.dto.CurrencyData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "invertexto-api", url = "https://api.invertexto.com/v1/currency")
public interface CurrencyConverterClient {

    @GetMapping("/{symbols}")
    Map<String, CurrencyData> getCurrencyConversion(
            @PathVariable("symbols") String symbols,
            @RequestParam("token") String token
    );
}