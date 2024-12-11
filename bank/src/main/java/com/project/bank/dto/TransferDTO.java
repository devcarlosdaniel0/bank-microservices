package com.project.bank.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferDTO(@NotNull String receiverAccountEmail,
                          @NotNull BigDecimal value) {
}
