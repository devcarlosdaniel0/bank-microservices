package com.project.bank.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CreateBankAccountDTO(@NotBlank @Length(min = 3, max = 3) String currencyCode) {
}
