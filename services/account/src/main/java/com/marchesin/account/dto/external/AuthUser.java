package com.marchesin.account.dto.external;

public record AuthUser(
        String id,
        String email,
        String name,
        boolean isEmailVerified
) {}