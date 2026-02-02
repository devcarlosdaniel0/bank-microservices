package com.marchesin.account.client;

import com.marchesin.account.dto.CurrencyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "currency-converter-service")
public interface CurrencyConverterFeignClient {

    @GetMapping("/convert-currencies/{symbols}")
    CurrencyResponse convertCurrencies(@PathVariable("symbols") String symbols,
                                       @RequestParam("amount") BigDecimal amount);
}
