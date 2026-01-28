package com.marchesin.account.dto;

import java.math.BigDecimal;

public record TransferResponse(
        String fromAccountId,
        BigDecimal amount,
        String currencyCode,
        String toAccountId
) {
}
