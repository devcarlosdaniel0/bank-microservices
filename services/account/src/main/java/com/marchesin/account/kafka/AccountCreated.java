package com.marchesin.account.kafka;

public record AccountCreated(
        String accountId,
        String currencyCode
) {
}
