package com.marchesin.balance.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_balance")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @Column(unique = true)
    private String accountId;

    @NotNull
    @Column(unique = true)
    private String userId;

    @NotNull
    @Column(length = 3)
    @Setter
    private String currencyCode;

    @NotNull
    private BigDecimal amount;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    public Balance(String accountId, String userId, String currencyCode, BigDecimal amount) {
        this.accountId = accountId;
        this.userId = userId;
        this.currencyCode = currencyCode;
        this.amount = amount;
    }
}