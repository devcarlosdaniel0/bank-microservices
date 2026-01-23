package com.marchesin.balance.service;

import com.marchesin.balance.domain.Balance;
import com.marchesin.balance.dto.BalanceResponse;
import com.marchesin.balance.kafka.Account;
import com.marchesin.balance.mapper.BalanceMapper;
import com.marchesin.balance.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {
    private final BalanceRepository repository;
    private final BalanceMapper mapper;

    @Transactional
    public void createInitialBalance(Account account) {
        if (repository.existsByAccountId(account.accountId())) {
            throw new RuntimeException("Saldo já existe para conta:: " + account.accountId());
        }

        Balance balance = Balance.builder()
                .accountId(account.accountId())
                .userId(account.userId())
                .amount(BigDecimal.ZERO)
                .currencyCode(account.currencyCode())
                .build();

        repository.save(balance);
        log.info("Initial balance of 0.00 {} created to account ID: {}", account.currencyCode(), account.accountId());
    }

    public BalanceResponse getBalance(String userId) {
        Balance balance = repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Balance not found"));

        return mapper.fromBalance(balance);
    }

    @Transactional
    public void deleteBalance(String accountId) {
        if (repository.existsByAccountId(accountId)) {
            repository.deleteByAccountId(accountId);
            log.info("Balance deleted for account ID: {}", accountId);
        }
    }
}
