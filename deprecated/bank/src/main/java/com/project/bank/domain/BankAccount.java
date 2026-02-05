package com.project.bank.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Entity
@Table(name = "tb_bank_account")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccount  {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    private String keycloakUserId;

    @NotNull
    @Email
    private String accountEmail;

    @NotNull
    private String accountName;

    @NotNull
    private BigDecimal balance = BigDecimal.ZERO;

    @NotNull
    private Currency currency;
}
