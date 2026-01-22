package com.marchesin.balance.kafka;

public record Account(
        String accountId,
        String userId,
        String currencyCode
) {
}

