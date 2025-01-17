package com.project.core.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@Entity
@Table(name = "tb_bank_account")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    @NotNull
    @ToString.Exclude
    private UserEntity user;

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
