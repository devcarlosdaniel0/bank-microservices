package com.project.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountResponseDTO {
    private UUID id;
    private BigDecimal balance;
    private Long userId;
}
