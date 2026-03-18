package com.marchesin.account.kafka;

import com.marchesin.account.kafka.enums.TransactionType;

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
        String targetEmail,
        LocalDateTime timestamp
) {
}