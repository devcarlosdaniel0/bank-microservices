package com.marchesin.account.mapper;

import com.marchesin.account.domain.Account;
import com.marchesin.account.dto.AccountResponse;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse fromAccount(Account account) {
        if (account == null) {
            return null;
        }

        return new AccountResponse(
                account.getId(),
                account.getUserId(),
                account.getCurrency(),
                account.getCreatedAt()
        );
    }
}
