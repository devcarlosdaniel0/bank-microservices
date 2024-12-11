package com.project.bank.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferDTO(@NotNull UUID receiverAccountId,
                          @NotNull BigDecimal value) {
}
