package com.project.currency_converter.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record CurrencyResponse(String symbols, BigDecimal price, LocalDateTime timestamp) {
}
