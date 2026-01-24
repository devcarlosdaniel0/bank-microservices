package com.marchesin.account.service;

import com.marchesin.account.domain.Account;
import com.marchesin.account.domain.CurrencyCode;
import com.marchesin.account.dto.AccountResponse;
import com.marchesin.account.dto.AuthenticatedUser;
import com.marchesin.account.dto.CreateAccountRequest;
import com.marchesin.account.dto.UpdateAccountRequest;
import com.marchesin.account.exception.AccountNotFound;
import com.marchesin.account.exception.UserAlreadyHasAccount;
import com.marchesin.account.exception.UserEmailNotVerified;
import com.marchesin.account.kafka.AccountTopicSent;
import com.marchesin.account.kafka.AccountProducer;
import com.marchesin.account.mapper.AccountMapper;
import com.marchesin.account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountRepository repository;
    private final AccountMapper mapper;
    private final AccountProducer producer;

    public AccountService(AccountRepository repository, AccountMapper mapper, AccountProducer producer) {
        this.repository = repository;
        this.mapper = mapper;
        this.producer = producer;
    }

    @Transactional
    public AccountResponse createAccount(AuthenticatedUser user, CreateAccountRequest request) {
        if (repository.existsByUserId(user.id())) {
            throw new UserAlreadyHasAccount("User already has an account");
        }

        if (!user.isEmailVerified()) {
            throw new UserEmailNotVerified("User email is not verified");
        }

        Account account = new Account(user.id(), new CurrencyCode(request.currencyCode()));

        Account savedAccount = repository.save(account);

        AccountTopicSent accountTopicSent = new AccountTopicSent(
                savedAccount.getId(),
                savedAccount.getUserId(),
                savedAccount.getCurrencyCode()
        );

        producer.sendAccountCreated(accountTopicSent);

        return mapper.fromAccount(savedAccount);
    }

    // TODO: Método precisa atualizar o Balance e depois fazer uma conversão de moedas no Balance Service

    @Transactional
    public AccountResponse updateAccount(AuthenticatedUser user, UpdateAccountRequest request) {
        Account account = repository.findByUserId(user.id())
                .orElseThrow(() -> new AccountNotFound("Account was not found"));

        account.changeCurrency(new CurrencyCode(request.currencyCode()));

        AccountTopicSent accountTopicSent = new AccountTopicSent(
                account.getId(),
                account.getUserId(),
                account.getCurrencyCode()
        );

        producer.sendAccountUpdated(accountTopicSent);

        return mapper.fromAccount(account);
    }

    public Page<AccountResponse> findAll(Pageable pageable) {
        Page<Account> accounts = repository.findAll(pageable);

        return accounts.map(mapper::fromAccount);
    }

    @Transactional
    public void deleteAccount(AuthenticatedUser user) {
        Account account = repository.findByUserId(user.id())
                .orElseThrow(() -> new AccountNotFound("Account was not found"));

        repository.delete(account);

        producer.sendAccountDeleted(account.getId());
    }
}
