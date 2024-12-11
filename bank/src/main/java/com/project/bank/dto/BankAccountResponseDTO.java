package com.project.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BankAccountResponseDTO {
    private UUID id;
    private BigDecimal balance;
    private Long userId;
    private String email;
    private String accountName;
}
