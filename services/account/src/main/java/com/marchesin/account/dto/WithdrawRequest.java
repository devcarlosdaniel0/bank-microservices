package com.marchesin.account.dto;

import java.math.BigDecimal;

public record WithdrawRequest (
        BigDecimal amount
) {
}
