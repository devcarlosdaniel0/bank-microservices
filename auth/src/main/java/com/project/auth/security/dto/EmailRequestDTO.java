package com.project.auth.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequestDTO(@NotBlank @Email String email) {
}
