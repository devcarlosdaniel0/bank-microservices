package com.marchesin.transaction.domain;

import com.marchesin.transaction.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_transaction")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String accountId;

    @Enumerated(value = EnumType.STRING)
    private TransactionType type;

    private BigDecimal amount;
    private String currencyCode;
    private LocalDateTime createdAt;

    public Transaction(String accountId, TransactionType type, BigDecimal amount, String currencyCode, LocalDateTime createdAt) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.createdAt = createdAt;
    }
}
