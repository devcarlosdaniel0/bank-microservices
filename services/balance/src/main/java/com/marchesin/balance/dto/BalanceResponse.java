package com.marchesin.balance.dto;

import java.math.BigDecimal;

public record BalanceResponse(
        String accountId,
        BigDecimal amount,
        String currencyCode
) {
}
