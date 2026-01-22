package com.marchesin.balance.kafka;

public record AccountCreated(
        String accountId,
        String currencyCode
) {
}

