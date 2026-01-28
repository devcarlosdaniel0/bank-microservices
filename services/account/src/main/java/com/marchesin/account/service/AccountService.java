package com.marchesin.account.service;

import com.marchesin.account.domain.Account;
import com.marchesin.account.domain.CurrencyCode;
import com.marchesin.account.dto.*;
import com.marchesin.account.exception.AccountNotFound;
import com.marchesin.account.exception.SameAccountTransfer;
import com.marchesin.account.exception.UserAlreadyHasAccount;
import com.marchesin.account.exception.UserEmailNotVerified;
import com.marchesin.account.mapper.AccountMapper;
import com.marchesin.account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {
    private final AccountRepository repository;
    private final AccountMapper mapper;

    public AccountService(AccountRepository repository, AccountMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
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

    // TODO -> chamar exchange currency para converter o saldo atual
    @Transactional
    public AccountResponse updateAccount(String userId, UpdateAccountRequest request) {
        Account account = getAccountFromUserId(userId);

        account.changeCurrency(new CurrencyCode(request.currencyCode()));

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

        return new BalanceResponse(account.getBalanceAmount(), account.getCurrencyCode());
    }

    @Transactional
    public BalanceResponse withdraw(String userId, WithdrawRequest request) {
        Account account = getAccountFromUserId(userId);

        account.withdraw(request.amount());

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
        to.deposit(request.amount());

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
