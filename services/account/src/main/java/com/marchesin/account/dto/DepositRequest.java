package com.marchesin.account.dto;

import java.math.BigDecimal;

public record DepositRequest(
        BigDecimal amount
) {
}
