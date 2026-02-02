package com.marchesin.account.domain;

import com.marchesin.account.exception.InvalidCurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Currency;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurrencyCode {

    @Column(name = "currency_code", length = 3, nullable = false)
    @Getter
    private String value;

    public CurrencyCode(String value) {
        String normalized = this.normalize(value);

        this.validate(normalized);

        this.value = normalized;
    }

    private String normalize(String value) {
        return value.toUpperCase().trim();
    }

    private void validate(String normalized) {
        try {
            Currency.getInstance(normalized);
        } catch (IllegalArgumentException e) {
            throw new InvalidCurrencyCode("Invalid currency code: " + normalized);
        }
    }

}
