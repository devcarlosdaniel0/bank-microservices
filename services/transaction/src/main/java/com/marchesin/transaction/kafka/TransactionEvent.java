package com.marchesin.transaction.kafka;

import com.marchesin.transaction.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionEvent (
        TransactionType type,
        BigDecimal sourceAmount,
        BigDecimal targetAmount,
        String sourceCurrency,
        String targetCurrency,
        BigDecimal exchangeRate,
        String sourceAccountId,
        String targetAccountId,
        String sourceEmail,
        String targetEmail,
        LocalDateTime timestamp
) {
}
