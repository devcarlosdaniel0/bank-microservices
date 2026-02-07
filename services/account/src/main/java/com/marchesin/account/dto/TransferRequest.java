package com.marchesin.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
        @NotNull
        String toEmail,
        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount
) {
}
