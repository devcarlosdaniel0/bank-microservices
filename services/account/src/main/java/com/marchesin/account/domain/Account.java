package com.marchesin.account.domain;

import com.marchesin.account.exception.SameCurrencyException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_account")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private String id;

    @NotNull
    @Column(unique = true)
    @Getter
    private String userId;

    @NotNull
    @Embedded
    private CurrencyCode currencyCode;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    @Getter
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    @Getter
    private LocalDateTime lastModifiedDate;

    public Account(String userId, CurrencyCode currencyCode) {
        this.userId = userId;
        this.currencyCode = currencyCode;
    }

    public void changeCurrency(CurrencyCode newCurrency) {
        if (newCurrency.equals(this.currencyCode)) {
            throw new SameCurrencyException("The account already has this currency");
        }

        this.currencyCode = newCurrency;
    }

    public String getCurrencyCode() {
        return this.currencyCode.getValue();
    }

}
