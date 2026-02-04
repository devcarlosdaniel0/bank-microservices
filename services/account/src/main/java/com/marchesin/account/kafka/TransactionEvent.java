package com.marchesin.account.kafka;

import com.marchesin.account.kafka.enums.TransactionType;

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
