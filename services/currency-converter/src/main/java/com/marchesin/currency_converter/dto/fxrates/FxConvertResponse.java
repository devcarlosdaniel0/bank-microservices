package com.marchesin.currency_converter.dto.fxrates;

import java.math.BigDecimal;

public record FxConvertResponse(
        boolean success,
        Query query,
        Info info,
        boolean historical,
        String date,
        Long timestamp,
        BigDecimal result
) {}
