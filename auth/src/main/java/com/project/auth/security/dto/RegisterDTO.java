package com.project.auth.security.dto;

import com.project.auth.security.domain.UserRole;

public record RegisterDTO(String username, String password, UserRole role) {
}
