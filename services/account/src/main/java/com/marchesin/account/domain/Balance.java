package com.marchesin.account.domain;

import com.marchesin.account.exception.InsufficientFunds;
import com.marchesin.account.exception.InvalidAmount;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Balance {

    @NotNull
    @Column(name = "balance_amount")
    private BigDecimal amount;

    private Balance(BigDecimal amount) {
        this.amount = amount;
    }

    public static Balance zero() {
        return new Balance(BigDecimal.ZERO);
    }

    public void add(BigDecimal amount) {
        validatePositiveAmount(amount);
        this.amount = this.amount.add(amount);
    }

    public void subtract(BigDecimal amount) {
        validatePositiveAmount(amount);
        validateSufficientFunds(amount);
        this.amount = this.amount.subtract(amount);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmount("Amount cant be negative or zero");
        }
    }

    private void validateSufficientFunds(BigDecimal amount) {
        if (this.amount.compareTo(amount) < 0) {
            throw new InsufficientFunds("Insufficient funds");
        }
    }
}
