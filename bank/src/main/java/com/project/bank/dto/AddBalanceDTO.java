package com.project.bank.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AddBalanceDTO (@NotNull UUID accountId, @NotNull BigDecimal value) {
}
