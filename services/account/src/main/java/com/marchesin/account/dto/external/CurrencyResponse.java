package com.marchesin.account.dto.external;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CurrencyResponse(String symbols, BigDecimal exchangeRate, BigDecimal amount, BigDecimal convertedAmount,
                               LocalDateTime timestamp) {
}
