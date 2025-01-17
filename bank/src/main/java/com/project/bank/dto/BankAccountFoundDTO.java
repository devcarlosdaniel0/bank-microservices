package com.project.bank.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BankAccountFoundDTO(@NotNull UUID accountId) {
}
