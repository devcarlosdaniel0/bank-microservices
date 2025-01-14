package com.project.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BankAccountResponseDTO {
    private UUID id;
    private Long userId;
    private String email;
    private String accountName;
    private BigDecimal balance;
    private Currency currency;
}
