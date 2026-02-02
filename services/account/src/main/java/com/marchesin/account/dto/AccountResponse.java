package com.marchesin.account.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        String id,
        String userId,
        String currencyCode,
        BigDecimal balance,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedDate
) {
}
