package com.project.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferDTO(@NotNull @Schema(description = "The receiver account email it's the email that you want to transfer",
                        example = "johndoe@gmail.com")
                          String receiverAccountEmail,
                          @NotNull BigDecimal value) {
}
