package com.marchesin.currency_converter.dto.fxrates;

import java.math.BigDecimal;

public record Query(
        String from,
        String to,
        BigDecimal amount
) {}