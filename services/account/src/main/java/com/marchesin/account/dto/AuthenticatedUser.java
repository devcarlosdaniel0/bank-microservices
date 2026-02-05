package com.marchesin.account.dto;

public record AuthenticatedUser(
        String id,
        String email,
        String name,
        boolean isEmailVerified
) {}