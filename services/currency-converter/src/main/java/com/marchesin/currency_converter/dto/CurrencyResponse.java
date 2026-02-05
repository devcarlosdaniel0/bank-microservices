package com.marchesin.currency_converter.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CurrencyResponse(String symbols, BigDecimal exchangeRate, BigDecimal amount, BigDecimal convertedAmount,
                               LocalDateTime timestamp) {
}
