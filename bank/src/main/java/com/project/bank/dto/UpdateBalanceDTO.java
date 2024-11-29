package com.project.bank.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateBalanceDTO(@NotNull UUID accountId, @NotNull BigDecimal value) {
}
