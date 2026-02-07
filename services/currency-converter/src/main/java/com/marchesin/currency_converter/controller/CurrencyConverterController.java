package com.marchesin.currency_converter.controller;

import com.marchesin.currency_converter.dto.CurrencyResponse;
import com.marchesin.currency_converter.service.impl.CurrencyConverterService;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@Validated
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
            @RequestParam @DecimalMin("0.01") BigDecimal amount) {
        return new ResponseEntity<>(currencyConverterService.convert(from, to, amount), HttpStatus.OK);
    }

}
