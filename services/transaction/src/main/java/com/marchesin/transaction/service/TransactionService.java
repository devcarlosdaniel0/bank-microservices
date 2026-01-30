package com.marchesin.transaction.service;

import com.marchesin.transaction.domain.Transaction;
import com.marchesin.transaction.kafka.TransactionEvent;
import com.marchesin.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void createTransaction(TransactionEvent event) {
        Transaction transaction = new Transaction(event.accountId(), event.type(), event.amount(), event.currencyCode(), event.createdAt());

        repository.save(transaction);
    }
}
