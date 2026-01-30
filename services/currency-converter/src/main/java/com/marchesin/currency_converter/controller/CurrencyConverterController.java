package com.marchesin.currency_converter.controller;

import com.marchesin.currency_converter.dto.CurrencyResponse;
import com.marchesin.currency_converter.service.CurrencyConverterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class CurrencyConverterController {
    private final CurrencyConverterService currencyConverterService;

    public CurrencyConverterController(CurrencyConverterService currencyConverterService) {
        this.currencyConverterService = currencyConverterService;
    }

    @GetMapping("/convert-currencies/{symbols}")
    public ResponseEntity<CurrencyResponse> convertCurrencies(@RequestParam BigDecimal amount,
                                                              @PathVariable String symbols) {
        return new ResponseEntity<>(currencyConverterService.convertCurrencies(amount, symbols), HttpStatus.OK);
    }
}
