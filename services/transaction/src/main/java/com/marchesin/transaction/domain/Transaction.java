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

    @Enumerated(value = EnumType.STRING)
    private TransactionType type;

    private BigDecimal sourceAmount;

    private BigDecimal targetAmount;

    private String sourceCurrency;

    private String targetCurrency;

    private BigDecimal exchangeRate;

    private String sourceAccountId;

    private String targetAccountId;

    private String targetEmail;

    private LocalDateTime timeStamp;

    public Transaction(TransactionType type, BigDecimal sourceAmount, BigDecimal targetAmount, String sourceCurrency, String targetCurrency, BigDecimal exchangeRate, String sourceAccountId, String targetAccountId, String targetEmail, LocalDateTime timeStamp) {
        this.type = type;
        this.sourceAmount = sourceAmount;
        this.targetAmount = targetAmount;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.exchangeRate = exchangeRate;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.targetEmail = targetEmail;
        this.timeStamp = timeStamp;
    }
}
