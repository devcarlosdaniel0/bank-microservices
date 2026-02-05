package com.project.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransferResponseDTO {
    private BigDecimal senderCurrentBalance;
    private String senderCurrencyCode;
    private BigDecimal transferredValue;
    private String receiverName;
    private String receiverEmail;
    private BigDecimal convertedAmount;
    private String receiverCurrencyCode;
}
