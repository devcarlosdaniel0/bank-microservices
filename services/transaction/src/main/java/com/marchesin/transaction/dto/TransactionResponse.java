package com.marchesin.transaction.dto;

import com.marchesin.transaction.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        BigDecimal amount,
        String currencyCode,
        TransactionType type,
        LocalDateTime createdAt
) {
}
