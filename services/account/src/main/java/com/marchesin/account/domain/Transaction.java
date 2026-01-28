package com.marchesin.account.domain;

import com.marchesin.account.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_transaction")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    private String accountId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @NotNull
    private BigDecimal amount;

    @NotNull
    @Column(length = 3)
    private String currencyCode;

    @NotNull
    private BigDecimal balanceAfter;

    private String correlationId;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    private Transaction(String accountId, TransactionType type, BigDecimal amount, String currencyCode, BigDecimal balanceAfter, String correlationId) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.balanceAfter = balanceAfter;
        this.correlationId = correlationId;
    }

    public static Transaction createTransaction(Account account, TransactionType type, BigDecimal amount, String correlationId) {
        return new Transaction(
                account.getId(),
                type,
                amount,
                account.getCurrencyCode(),
                account.getBalanceAmount(),
                correlationId
        );
    }

}
