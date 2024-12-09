package com.project.bank.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BankAccountFoundedDTO (@NotNull UUID accountId) {
}
