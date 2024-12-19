package com.project.auth.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(@NotBlank @Email String email,
                          @NotBlank String username,
                          @NotBlank String password) {
}
