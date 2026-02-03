package com.marchesin.account.service;

import com.marchesin.account.domain.Account;
import com.marchesin.account.domain.CurrencyCode;
import com.marchesin.account.dto.*;
import com.marchesin.account.enums.TransactionType;
import com.marchesin.account.exception.AccountNotFound;
import com.marchesin.account.exception.UserAlreadyHasAccount;
import com.marchesin.account.exception.UserEmailNotVerified;
import com.marchesin.account.kafka.AccountProducer;
import com.marchesin.account.kafka.TransactionEvent;
import com.marchesin.account.mapper.AccountMapper;
import com.marchesin.account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AccountService {
    private final AccountRepository repository;
    private final AccountMapper mapper;
    private final AccountProducer producer;
    private final CurrencyConversionService conversionService;

    public AccountService(AccountRepository repository, AccountMapper mapper, AccountProducer producer, CurrencyConversionService conversionService) {
        this.repository = repository;
        this.mapper = mapper;
        this.producer = producer;
        this.conversionService = conversionService;
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

        return mapper.fromAccount(savedAccount);
    }

    @Transactional
    public AccountResponse updateAccount(String userId, UpdateAccountRequest request) {
        Account account = getAccountFromUserId(userId);

        CurrencyCode actualCurrency = new CurrencyCode(account.getCurrencyCode());
        CurrencyCode newCurrency = new CurrencyCode(request.currencyCode());

        BigDecimal converted = conversionService.convert(actualCurrency, newCurrency, account.getBalanceAmount());

        account.changeCurrency(newCurrency, converted);

        return mapper.fromAccount(account);
    }

    public Page<AccountResponse> findAll(Pageable pageable) {
        Page<Account> accounts = repository.findAll(pageable);

        return accounts.map(mapper::fromAccount);
    }

    @Transactional
    public void deleteAccount(String userId) {
        Account account = getAccountFromUserId(userId);

        repository.delete(account);
    }

    public BalanceResponse getBalance(String userId) {
        Account account = getAccountFromUserId(userId);

        return new BalanceResponse(account.getBalanceAmount(), account.getCurrencyCode());
    }

    @Transactional
    public BalanceResponse deposit(String userId, DepositRequest request) {
        Account account = getAccountFromUserId(userId);

        account.deposit(request.amount());

        TransactionEvent event = new TransactionEvent(account.getId(), TransactionType.DEPOSIT, request.amount(), account.getCurrencyCode(), LocalDateTime.now());

        producer.sendTransactionEvent(event);

        return new BalanceResponse(account.getBalanceAmount(), account.getCurrencyCode());
    }

    @Transactional
    public BalanceResponse withdraw(String userId, WithdrawRequest request) {
        Account account = getAccountFromUserId(userId);

        account.withdraw(request.amount());

        TransactionEvent event = new TransactionEvent(account.getId(), TransactionType.WITHDRAW, request.amount(), account.getCurrencyCode(), LocalDateTime.now());

        producer.sendTransactionEvent(event);

        return new BalanceResponse(account.getBalanceAmount(), account.getCurrencyCode());
    }

    private Account getAccountFromUserId(String userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFound("Account not found"));
    }
}
