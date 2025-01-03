package com.project.currency_converter.controller;

import com.project.currency_converter.dto.CurrencyResponse;
import com.project.currency_converter.service.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CurrencyConverterController {
    private final CurrencyConverterService currencyConverterService;

    @GetMapping("/convertCurrencies/{symbols}")
    public ResponseEntity<CurrencyResponse> convertCurrencies(@PathVariable String symbols) {
        return new ResponseEntity<>(currencyConverterService.convertCurrencies(symbols), HttpStatus.OK);
    }
}
