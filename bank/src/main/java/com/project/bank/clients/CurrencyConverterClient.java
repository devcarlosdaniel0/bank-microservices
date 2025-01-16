package com.project.bank.clients;

import com.project.bank.dto.CurrencyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "currency-converter-api")
public interface CurrencyConverterClient {
    @GetMapping("/convertCurrencies/{symbols}")
    CurrencyResponse convertCurrencies(@RequestParam BigDecimal amount,
                                       @PathVariable String symbols);
}
