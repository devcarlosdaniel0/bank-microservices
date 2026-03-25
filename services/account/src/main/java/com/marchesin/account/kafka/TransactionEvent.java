package com.marchesin.account.kafka;

import com.marchesin.account.kafka.enums.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
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