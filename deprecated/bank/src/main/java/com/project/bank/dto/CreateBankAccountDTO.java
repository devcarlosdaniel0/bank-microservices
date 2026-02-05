package com.project.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;


public record CreateBankAccountDTO(@NotBlank @Length(min = 3, max = 3)
                                   @Schema(description = "Choose a currency for your bank account according to ISO 4127",
                                           example = "BRL")
                                   String currencyCode) {
}
