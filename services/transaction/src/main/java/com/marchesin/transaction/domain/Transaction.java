package com.marchesin.transaction.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String accountId;
    private String type;
    private BigDecimal amount;
    private String currencyCode;
    private LocalDateTime createdAt;

    public Transaction(String accountId, String type, BigDecimal amount, String currencyCode, LocalDateTime createdAt) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.createdAt = createdAt;
    }
}
