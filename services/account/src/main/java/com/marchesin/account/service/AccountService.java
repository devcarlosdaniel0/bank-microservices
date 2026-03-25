package com.marchesin.account.service;

import com.marchesin.account.domain.Account;
import com.marchesin.account.domain.CurrencyCode;
import com.marchesin.account.dto.*;
import com.marchesin.account.dto.external.AuthUser;
import com.marchesin.account.dto.external.CurrencyResponse;
import com.marchesin.account.exception.AccountNotFound;
import com.marchesin.account.exception.SameCurrencyException;
import com.marchesin.account.exception.UserAlreadyHasAccount;
import com.marchesin.account.exception.UserEmailNotVerified;
import com.marchesin.account.kafka.AccountProducer;
import com.marchesin.account.kafka.factory.TransactionFactory;
import com.marchesin.account.mapper.AccountMapper;
import com.marchesin.account.repository.AccountRepository;
import com.marchesin.account.service.external.CurrencyConverterService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {
    private final AccountRepository repository;
    private final AccountMapper mapper;
    private final CurrencyConverterService conversionService;
    private final AccountProducer producer;
    private final TransactionFactory factory;

    public AccountService(AccountRepository repository, AccountMapper mapper, CurrencyConverterService conversionService, AccountProducer producer, TransactionFactory factory) {
        this.repository = repository;
        this.mapper = mapper;
        this.conversionService = conversionService;
        this.producer = producer;
        this.factory = factory;
    }

    @Transactional
    public AccountResponse createAccount(AuthUser user, CreateAccountRequest request) {
        if (repository.existsByUserId(user.id())) {
            throw new UserAlreadyHasAccount("User already has an account");
        }

        if (!user.isEmailVerified()) {
            throw new UserEmailNotVerified("User email is not verified");
        }

        Account account = new Account(user.id(), new CurrencyCode(request.currencyCode()));

        Account savedAccount = repository.save(account);

        return mapper.fromAccount(savedAccount);
    }

    @Transactional
    public AccountResponse updateAccount(String userId, UpdateAccountRequest request) {
        Account account = findByUserId(userId);

        BigDecimal oldBalance = account.getBalanceAmount();
        CurrencyCode oldCurrency = new CurrencyCode(account.getCurrencyCode());
        CurrencyCode newCurrency = new CurrencyCode(request.currencyCode());

        if (oldCurrency.equals(newCurrency)) {
            throw new SameCurrencyException("Account already uses this currency");
        }

        CurrencyResponse response = conversionService.convert(oldCurrency, newCurrency, oldBalance);
        BigDecimal newBalance = response.convertedAmount();

        account.changeCurrency(newCurrency, newBalance);

        producer.sendTransactionEvent(factory.createExchange(account, oldBalance, newBalance, oldCurrency.getValue(), newCurrency.getValue(), response.exchangeRate()));

        return mapper.fromAccount(account);
    }

    public Page<AccountResponse> findAll(Pageable pageable) {
        Page<Account> accounts = repository.findAll(pageable);

        return accounts.map(mapper::fromAccount);
    }

    @Transactional
    public void deleteAccount(String userId) {
        Account account = findByUserId(userId);

        repository.delete(account);
    }

    public BalanceResponse getBalance(String userId) {
        Account account = findByUserId(userId);

        return new BalanceResponse(account.getBalanceAmount(), account.getCurrencyCode());
    }

    public String getAccountIdByUserId(String userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFound("Account not found")).getId();
    }

    public Account findByUserId(String userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFound("Account not found"));
    }
}
