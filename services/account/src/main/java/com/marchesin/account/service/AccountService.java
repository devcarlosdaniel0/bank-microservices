package com.marchesin.account.service;

import com.marchesin.account.client.CurrencyConverterFeignClient;
import com.marchesin.account.domain.Account;
import com.marchesin.account.domain.CurrencyCode;
import com.marchesin.account.dto.*;
import com.marchesin.account.enums.TransactionType;
import com.marchesin.account.exception.AccountNotFound;
import com.marchesin.account.exception.SameAccountTransfer;
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
    private final CurrencyConverterFeignClient client;

    public AccountService(AccountRepository repository, AccountMapper mapper, AccountProducer producer, CurrencyConverterFeignClient client) {
        this.repository = repository;
        this.mapper = mapper;
        this.producer = producer;
        this.client = client;
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

        CurrencyResponse response = client.convert(account.getCurrencyCode(), request.currencyCode(), account.getBalanceAmount());

        account.changeCurrency(new CurrencyCode(request.currencyCode()), response.convertedAmount());

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

    @Transactional
    // TODO call exchange currency
    public TransferResponse transfer(String userId, TransferRequest request) {
        Account from = getAccountFromUserId(userId);
        Account to = getAccountFromId(request.toAccountId());

        if (from.getId().equals(to.getId())) {
            throw new SameAccountTransfer("Cannot transfer to the same account");
        }

        from.withdraw(request.amount());

        CurrencyResponse response = client.convert(from.getCurrencyCode(), to.getCurrencyCode(), request.amount());
        BigDecimal convertedAmount = response.convertedAmount();

        to.deposit(convertedAmount);

        TransactionEvent eventFrom = new TransactionEvent(from.getId(), TransactionType.TRANSFER_OUT, request.amount(), from.getCurrencyCode(), LocalDateTime.now());
        TransactionEvent eventTo = new TransactionEvent(to.getId(), TransactionType.TRANSFER_IN, convertedAmount, to.getCurrencyCode(), LocalDateTime.now());

        producer.sendTransactionEvent(eventFrom);
        producer.sendTransactionEvent(eventTo);

        return new TransferResponse(from.getId(), request.amount(), from.getCurrencyCode(), to.getId());
    }

    private Account getAccountFromUserId(String userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFound("Account not found"));
    }

    private Account getAccountFromId(String AccountId) {
        return repository.findById(AccountId)
                .orElseThrow(() -> new AccountNotFound("Account not found"));
    }
}
