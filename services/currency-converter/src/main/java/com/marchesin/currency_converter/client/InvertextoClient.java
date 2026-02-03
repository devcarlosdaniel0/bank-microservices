package com.marchesin.currency_converter.client;

import com.marchesin.currency_converter.config.InvertextoFeignConfig;
import com.marchesin.currency_converter.dto.invertexto.InvertextoData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "invertexto-api",
        url = "https://api.invertexto.com/v1/currency",
        configuration = InvertextoFeignConfig.class)
public interface InvertextoClient {

    @GetMapping("/{symbols}")
    Map<String, InvertextoData> getCurrencyConversion(
            @PathVariable("symbols") String symbols
    );
}