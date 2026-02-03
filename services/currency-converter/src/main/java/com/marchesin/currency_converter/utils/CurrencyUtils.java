package com.marchesin.currency_converter.utils;

public final class CurrencyUtils {

    private CurrencyUtils() {
    }

    public static String normalize(String currency) {
        return currency.trim().toUpperCase();
    }

    public static String symbols(String from, String to) {
        return normalize(from) + "_" + normalize(to);
    }
}
