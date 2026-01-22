package com.marchesin.balance.service;

import com.marchesin.balance.domain.Balance;
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

    @Transactional
    public void createInitialBalance(String accountId, String currencyCode) {
        if (repository.existsByAccountId(accountId)) {
            throw new RuntimeException("Saldo já existe para conta:: " + accountId);
        }

        Balance balance = Balance.builder()
                .accountId(accountId)
                .amount(BigDecimal.ZERO)
                .currencyCode(currencyCode)
                .build();

        repository.save(balance);
        log.info("Initial balance of 0.00 {} created to account ID: {}", currencyCode, accountId);
    }
}
