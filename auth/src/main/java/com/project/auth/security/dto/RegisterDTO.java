package com.project.auth.security.dto;

import com.project.core.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDTO(@NotBlank @Email String email,
                          @NotBlank String username,
                          @NotBlank String password,
                          @NotNull UserRole role) {
}
