package com.marchesin.account.dto;

import java.time.LocalDateTime;

public record AccountResponse(
        String id,
        String userId,
        String currencyCode,
        LocalDateTime createdAt
) {
}
