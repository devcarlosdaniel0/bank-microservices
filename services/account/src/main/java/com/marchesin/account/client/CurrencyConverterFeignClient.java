package com.marchesin.account.client;

import com.marchesin.account.dto.CurrencyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "currency-converter-service")
public interface CurrencyConverterFeignClient {

    @GetMapping("/api/v1/currency-converter/{from}/{to}") CurrencyResponse convert(
            @PathVariable("from") String from,
            @PathVariable("to") String to,
            @RequestParam("amount") BigDecimal amount);
}