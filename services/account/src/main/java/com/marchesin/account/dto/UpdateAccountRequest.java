package com.marchesin.account.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccountRequest(
        @NotBlank(message = "Currency is mandatory and cant be empty or null")
        String currencyCode
) {
}
