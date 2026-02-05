package com.marchesin.currency_converter.client;

import com.marchesin.currency_converter.config.FxRatesFeignConfig;
import com.marchesin.currency_converter.dto.fxrates.FxConvertResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "fxrates-api",
        url = "https://api.fxratesapi.com",
        configuration = FxRatesFeignConfig.class)
public interface FxRatesClient {

    @GetMapping("/convert")
    FxConvertResponse convert(
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("amount") BigDecimal amount
    );
}