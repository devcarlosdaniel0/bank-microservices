package com.project.bank.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record CurrencyResponse(String symbols, BigDecimal exchangeRate, BigDecimal amount, BigDecimal convertedAmount,
                               LocalDateTime timestamp) {
}
