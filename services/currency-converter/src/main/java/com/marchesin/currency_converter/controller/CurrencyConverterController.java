package com.marchesin.currency_converter.controller;

import com.marchesin.currency_converter.dto.CurrencyResponse;
import com.marchesin.currency_converter.service.CurrencyConverterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/currency-converter")
public class CurrencyConverterController {
    private final CurrencyConverterService currencyConverterService;

    public CurrencyConverterController(CurrencyConverterService currencyConverterService) {
        this.currencyConverterService = currencyConverterService;
    }

    @GetMapping("/{from}/{to}")
    public ResponseEntity<CurrencyResponse> convertInvertexto(
            @PathVariable String from,
            @PathVariable String to,
            @RequestParam BigDecimal amount) {
        return new ResponseEntity<>(currencyConverterService.convert(from, to, amount), HttpStatus.OK);
    }

}
