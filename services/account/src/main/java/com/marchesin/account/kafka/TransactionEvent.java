package com.marchesin.account.kafka;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionEvent (
        String accountId,
        String type,
        BigDecimal amount,
        String currencyCode,
        LocalDateTime createdAt
) {
}
