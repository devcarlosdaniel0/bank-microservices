package com.marchesin.account.dto.external;

public record AuthenticatedUser(
        String id,
        String email,
        String name,
        boolean isEmailVerified
) {}