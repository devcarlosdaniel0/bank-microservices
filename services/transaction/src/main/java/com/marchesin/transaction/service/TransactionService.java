package com.marchesin.transaction.service;

import com.marchesin.transaction.domain.Transaction;
import com.marchesin.transaction.dto.TransactionResponse;
import com.marchesin.transaction.kafka.TransactionEvent;
import com.marchesin.transaction.mapper.TransactionMapper;
import com.marchesin.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repository;
    private final TransactionMapper mapper;

    public TransactionService(TransactionRepository repository, TransactionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public void createTransaction(TransactionEvent event) {
        Transaction transaction = new Transaction(event.accountId(), event.type(), event.amount(), event.currencyCode(), event.createdAt());

        repository.save(transaction);
    }

    public List<TransactionResponse> findAllTransactions(String accountId) {
        List<Transaction> transactions = repository.findAllByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return transactions.stream().map(mapper::fromTransaction).toList();
    }
}
