package com.marchesin.account.domain;

import java.math.BigDecimal;

public interface CurrencyConverter {
    BigDecimal convert(CurrencyCode from, CurrencyCode to, BigDecimal amount);
}
