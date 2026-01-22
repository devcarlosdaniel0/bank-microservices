package com.marchesin.account.service;

import com.marchesin.account.domain.Account;
import com.marchesin.account.dto.AccountResponse;
import com.marchesin.account.dto.AuthenticatedUser;
import com.marchesin.account.dto.CreateAccountRequest;
import com.marchesin.account.dto.UpdateAccountRequest;
import com.marchesin.account.exception.AccountNotFound;
import com.marchesin.account.exception.UserAlreadyHasAccount;
import com.marchesin.account.exception.UserEmailNotVerified;
import com.marchesin.account.kafka.AccountSent;
import com.marchesin.account.kafka.AccountProducer;
import com.marchesin.account.mapper.AccountMapper;
import com.marchesin.account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;
    private final AccountMapper mapper;
    private final AccountProducer producer;

    @Transactional
    public AccountResponse createAccount(AuthenticatedUser user, CreateAccountRequest request) {
        if (repository.existsByUserId(user.id())) {
            throw new UserAlreadyHasAccount("User already has an account");
        }

        if (!user.isEmailVerified()) {
            throw new UserEmailNotVerified("User email is not verified");
        }

        Account account = Account.builder()
                .userId(user.id())
                .currencyCode(request.currency().getCurrencyCode())
                .build();

        Account savedAccount = repository.save(account);

        AccountSent accountSent = new AccountSent(
                savedAccount.getId(),
                savedAccount.getUserId(),
                savedAccount.getCurrencyCode()
        );

        producer.sendAccount(accountSent);

        return mapper.fromAccount(savedAccount);
    }

    @Transactional
    public AccountResponse updateAccount(AuthenticatedUser user, UpdateAccountRequest request) {
        Account account = repository.findByUserId(user.id())
                .orElseThrow(() -> new AccountNotFound("Account was not found"));

        if (request.currency() != null) {
            account.setCurrencyCode(request.currency().getCurrencyCode());
        }

        return mapper.fromAccount(repository.save(account));
    }

    public Page<AccountResponse> findAll(Pageable pageable) {
        Page<Account> accounts = repository.findAll(pageable);

        return accounts.map(mapper::fromAccount);
    }
}
