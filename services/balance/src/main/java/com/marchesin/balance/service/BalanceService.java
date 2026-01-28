package com.marchesin.balance.service;

import com.marchesin.balance.domain.Balance;
import com.marchesin.balance.dto.*;
import com.marchesin.balance.exception.AmountCantBeNegativeOrZero;
import com.marchesin.balance.exception.BalanceAlreadyExists;
import com.marchesin.balance.exception.BalanceNotFound;
import com.marchesin.balance.exception.SameAccountTransfer;
import com.marchesin.balance.kafka.Account;
import com.marchesin.balance.mapper.BalanceMapper;
import com.marchesin.balance.repository.BalanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
public class BalanceService {
    private final BalanceRepository repository;
    private final BalanceMapper mapper;

    public BalanceService(BalanceRepository repository, BalanceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public void createInitialBalance(Account account) {
        if (repository.existsByAccountId(account.accountId())) {
            throw new BalanceAlreadyExists("Balance already exists for account ID: " + account.accountId());
        }

        Balance balance = new Balance(
                account.accountId(),
                account.userId(),
                account.currencyCode(),
                BigDecimal.ZERO
        );

        repository.save(balance);
        log.info("Initial balance of 0.00 {} created to account ID: {}", account.currencyCode(), account.accountId());
    }

    public BalanceResponse getBalance(String userId) {
        Balance balance = getBalanceFromUserId(userId);

        return mapper.fromBalance(balance);
    }

    public void deleteBalance(String accountId) {
        if (repository.existsByAccountId(accountId)) {
            repository.deleteByAccountId(accountId);
            log.info("Balance deleted for account ID: {}", accountId);
        }
    }

    @Transactional
    // TODO CHAMAR O CURRENCY CONVERTER
    public void updateBalance(Account account) {
        Balance balance = getBalanceFromAccountId(account.accountId());

        balance.setCurrencyCode(account.currencyCode());
    }

    @Transactional
    public BalanceResponse deposit(String userId, DepositRequest request) {
        Balance balance = getBalanceFromUserId(userId);

        balance.add(request.amount());

        return mapper.fromBalance(balance);
    }

    @Transactional
    public BalanceResponse withdraw(String userId, WithdrawalRequest request) {
        Balance balance = getBalanceFromUserId(userId);

        balance.subtract(request.amount());

        return mapper.fromBalance(balance);
    }

    @Transactional
    // TODO call exchange currency
    public TransferResponse transfer(String userId, TransferRequest request) {
        String toAccountId = request.toAccountId();
        BigDecimal amount = request.amount();

        Balance from = getBalanceFromUserId(userId);
        Balance to = getBalanceFromAccountId(toAccountId);

        if (from.getAccountId().equals(to.getAccountId())) {
            throw new SameAccountTransfer("Cannot transfer to the same account");
        }

        Balance.validatePositiveAmount(amount);

        from.validateSufficientFunds(amount);

        from.subtract(amount);
        to.add(amount);

        return new TransferResponse(from.getAccountId(), amount, from.getCurrencyCode(), to.getAccountId());
    }

    private Balance getBalanceFromUserId(String userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new BalanceNotFound(("Balance not found")));
    }

    private Balance getBalanceFromAccountId(String accountId) {
        return repository.findByAccountId(accountId)
                .orElseThrow(() -> new BalanceNotFound("Balance not found"));
    }
}
