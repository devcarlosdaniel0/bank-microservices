package com.project.bank.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BalanceResponseDTO(BigDecimal balance, String currency) {

}
