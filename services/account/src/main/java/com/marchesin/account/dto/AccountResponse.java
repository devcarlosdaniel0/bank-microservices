package com.marchesin.account.dto;

import java.time.LocalDateTime;
import java.util.Currency;

public record AccountResponse(
        String id,
        String userId,
        Currency currency,
        LocalDateTime createdAt
) {
}
