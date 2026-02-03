package com.marchesin.currency_converter.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TimeUtils {

    private TimeUtils() {

    }

    public static LocalDateTime getTimestampFormatted(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp),
                ZoneOffset.UTC);
    }
}
