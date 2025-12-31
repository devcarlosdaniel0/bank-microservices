package com.project.bank.domain;

public record AuthUser(Long id, String email, String username, boolean isConfirmed) {}