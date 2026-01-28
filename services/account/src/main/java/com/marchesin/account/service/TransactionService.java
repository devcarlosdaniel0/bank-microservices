package com.marchesin.account.service;

import com.marchesin.account.domain.Account;
import com.marchesin.account.domain.Transaction;
import com.marchesin.account.enums.TransactionType;
import com.marchesin.account.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public void saveDeposit(Account account, BigDecimal amount) {
        repository.save(Transaction.createTransaction(account, TransactionType.DEPOSIT, amount, null));
    }

    public void saveWithdraw(Account account, BigDecimal amount) {
        repository.save(Transaction.createTransaction(account, TransactionType.WITHDRAW, amount, null));
    }

    public void saveTransfer(
            Account from,
            Account to,
            BigDecimal amount
    ) {
        String correlationId = UUID.randomUUID().toString();

        repository.save(Transaction.createTransaction(from, TransactionType.TRANSFER_OUT, amount, correlationId));
        repository.save(Transaction.createTransaction(to, TransactionType.TRANSFER_IN, amount, correlationId));
    }
}
