package com.marchesin.account.kafka;

public record AccountSent(
        String accountId,
        String userId,
        String currencyCode
) {
}
