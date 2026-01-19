package com.marchesin.account.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Currency;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_account")
@EntityListeners(AuditingEntityListener.class)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    private String userId;

    @NotNull
    @Column(length = 3)
    @Getter(AccessLevel.NONE)
    private String currencyCode;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public Currency getCurrency() {
        return Currency.getInstance(currencyCode);
    }

    public void setCurrency(Currency currency) {
        this.currencyCode = currency.getCurrencyCode();
    }
}
