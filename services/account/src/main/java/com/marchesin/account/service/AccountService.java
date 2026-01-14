package com.marchesin.account.service;

import com.marchesin.account.domain.Account;
import com.marchesin.account.dto.AccountResponse;
import com.marchesin.account.dto.AuthenticatedUser;
import com.marchesin.account.dto.CreateAccountRequest;
import com.marchesin.account.exception.UserAlreadyHasAccount;
import com.marchesin.account.exception.UserEmailNotVerified;
import com.marchesin.account.mapper.AccountMapper;
import com.marchesin.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;
    private final AccountMapper mapper;

    public AccountResponse createAccount(AuthenticatedUser user, CreateAccountRequest request) {
        if (repository.existsByUserId(user.id())) {
            throw new UserAlreadyHasAccount("User already has an account");
        }

        if (!user.isEmailVerified()) {
            throw new UserEmailNotVerified("User email is not verified");
        }

        Account account = Account.builder()
                .userId(user.id())
                .ownerName(user.name())
                .email(user.email())
                .currency(request.currency())
                .build();

        return mapper.fromAccount(repository.save(account));
    }
}
