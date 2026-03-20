package com.marchesin.account.dto;

import java.math.BigDecimal;

public record TransferResponse(
        String fromAccountId,
        BigDecimal sentAmount,
        String fromCurrencyCode,
        String toEmail,
        BigDecimal receivedAmount,
        String toCurrencyCode
) {
}
