package com.project.currency_converter.controller;

import com.project.currency_converter.dto.CurrencyResponse;
import com.project.currency_converter.service.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class CurrencyConverterController {
    private final CurrencyConverterService currencyConverterService;

    @GetMapping("/convert-currencies/{symbols}")
    public ResponseEntity<CurrencyResponse> convertCurrencies(@RequestParam BigDecimal amount,
                                                              @PathVariable String symbols) {
        return new ResponseEntity<>(currencyConverterService.convertCurrencies(amount, symbols), HttpStatus.OK);
    }
}
