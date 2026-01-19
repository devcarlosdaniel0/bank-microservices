package com.marchesin.account.service;

import com.marchesin.account.domain.Account;
import com.marchesin.account.dto.AccountResponse;
import com.marchesin.account.dto.AuthenticatedUser;
import com.marchesin.account.dto.CreateAccountRequest;
import com.marchesin.account.dto.UpdateAccountRequest;
import com.marchesin.account.exception.AccountNotFound;
import com.marchesin.account.exception.UserAlreadyHasAccount;
import com.marchesin.account.exception.UserEmailNotVerified;
import com.marchesin.account.mapper.AccountMapper;
import com.marchesin.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;

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
                .build();

        account.setCurrency(request.currency());

        return mapper.fromAccount(repository.save(account));
    }

    public AccountResponse updateAccount(AuthenticatedUser user, UpdateAccountRequest request) {
        Account account = repository.findByUserId(user.id())
                .orElseThrow(() -> new AccountNotFound("Account was not found"));

        if (request.currency() != null) {
            account.setCurrency(request.currency());
        }

        return mapper.fromAccount(repository.save(account));
    }

    public Page<AccountResponse> findAll(Pageable pageable) {
        Page<Account> accounts = repository.findAll(pageable);

        return accounts.map(mapper::fromAccount);
    }
}
