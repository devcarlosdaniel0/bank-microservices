package com.marchesin.transaction.dto;

import com.marchesin.transaction.enums.TransactionDirection;
import com.marchesin.transaction.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        TransactionType type,
        TransactionDirection direction,
        BigDecimal sourceAmount,
        BigDecimal targetAmount,
        String sourceCurrency,
        String targetCurrency,
        BigDecimal exchangeRate,
        String sourceEmail,
        String targetEmail,
        LocalDateTime timestamp
) {
}
