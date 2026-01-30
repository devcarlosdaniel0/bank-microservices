package com.marchesin.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        BigDecimal amount,
        String currencyCode,
        String type,
        LocalDateTime createdAt
) {
}
