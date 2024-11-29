package com.project.bank.dto;

import jakarta.validation.constraints.NotNull;

public record CreateBankAccountDTO(@NotNull Long userId) {
}
