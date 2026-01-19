package com.marchesin.account.dto;

import java.util.Currency;

public record UpdateAccountRequest(
        Currency currency
) {
}
