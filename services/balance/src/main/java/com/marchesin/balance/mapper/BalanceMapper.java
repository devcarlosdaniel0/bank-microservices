package com.marchesin.balance.mapper;

import com.marchesin.balance.domain.Balance;
import com.marchesin.balance.dto.BalanceResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class BalanceMapper {

    public BalanceResponse fromBalance(@NonNull Balance balance) {

        return new BalanceResponse(
                balance.getAccountId(),
                balance.getAmount(),
                balance.getCurrencyCode()
        );
    }
}
