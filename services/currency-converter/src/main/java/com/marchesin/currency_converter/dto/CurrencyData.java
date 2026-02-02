package com.marchesin.currency_converter.dto;

import java.math.BigDecimal;

public record CurrencyData(BigDecimal price, long timestamp) {}
