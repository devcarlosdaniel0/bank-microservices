package com.marchesin.balance.service;

import com.marchesin.balance.domain.Balance;
import com.marchesin.balance.dto.BalanceResponse;
import com.marchesin.balance.dto.DepositRequest;
import com.marchesin.balance.exception.BalanceAlreadyExists;
import com.marchesin.balance.exception.BalanceNotFound;
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
        Balance balance = repository.findByUserId(userId)
                .orElseThrow(() -> new BalanceNotFound("Balance not found"));

        return mapper.fromBalance(balance);
    }

    public void deleteBalance(String accountId) {
        if (repository.existsByAccountId(accountId)) {
            repository.deleteByAccountId(accountId);
            log.info("Balance deleted for account ID: {}", accountId);
        }
    }

    @Transactional
    // TODO CONVERTER MOEDAS
    public void updateBalance(Account account) {
        Balance balance = repository.findByAccountId(account.accountId())
                .orElseThrow(() -> new BalanceNotFound("Balance not found"));

        balance.setCurrencyCode(account.currencyCode());
    }

    @Transactional
    public BalanceResponse deposit(String userId, DepositRequest request) {
        Balance balance = repository.findByUserId(userId)
                .orElseThrow(() -> new BalanceNotFound(("Balance not found")));

        balance.add(request.amount());

        return mapper.fromBalance(balance);
    }
}
