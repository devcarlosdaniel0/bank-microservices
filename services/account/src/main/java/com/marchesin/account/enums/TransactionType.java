package com.marchesin.account.enums;

import com.marchesin.account.kafka.TransactionEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public enum TransactionType {
    DEPOSIT,
    WITHDRAW,
    TRANSFER_IN,
    TRANSFER_OUT;

    public TransactionEvent create(String accountId, BigDecimal amount, String currencyCode) {
        return new TransactionEvent(
                accountId,
                this,
                amount,
                currencyCode,
                LocalDateTime.now()
        );
    }
}
