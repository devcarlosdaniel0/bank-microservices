package com.marchesin.account.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Currency;

public record CreateAccountRequest(
        @NotNull(message = "Currency is mandatory")
        Currency currency
) {
}
