package com.marchesin.user.domain;

public record AuthenticatedUser(
        String id,
        String email,
        String name,
        boolean isEmailVerified
) {}