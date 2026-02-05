package com.marchesin.account.mapper;

import com.marchesin.account.domain.Account;
import com.marchesin.account.dto.AccountResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse fromAccount(@NonNull Account account) {

        return new AccountResponse(
                account.getId(),
                account.getUserId(),
                account.getCurrencyCode(),
                account.getBalanceAmount(),
                account.getCreatedAt(),
                account.getLastModifiedDate()
        );
    }
}
