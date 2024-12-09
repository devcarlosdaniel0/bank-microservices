package com.project.bank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateBalanceDTO(@NotNull @DecimalMin(value = "0.01") BigDecimal value) {
}
