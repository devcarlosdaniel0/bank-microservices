package com.marchesin.transaction.kafka;

import com.marchesin.transaction.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionEvent (
        String accountId,
        TransactionType type,
        BigDecimal amount,
        String currencyCode,
        LocalDateTime createdAt
) {
}
