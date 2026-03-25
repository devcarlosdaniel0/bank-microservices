package com.marchesin.transaction.service;

import com.marchesin.transaction.client.AccountServiceClient;
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
    private final TransactionMapper mapper;
    private final TransactionRepository repository;
    private final AccountServiceClient client;

    public TransactionService(TransactionMapper mapper, TransactionRepository repository, AccountServiceClient client) {
        this.mapper = mapper;
        this.repository = repository;
        this.client = client;
    }

    @Transactional
    public void createTransaction(TransactionEvent event) {
        repository.save(mapper.fromEvent(event));
    }

    public List<TransactionResponse> findAllTransactions(String userId) {
        String accountId = client.getAccountIdFromUserId(userId);

        List<Transaction> transactions = repository.findAllBySourceAccountIdOrTargetAccountIdOrderByTimeStampDesc(accountId, accountId);

        return transactions.stream()
                .map(transaction -> mapper.fromTransaction(transaction, accountId))
                .toList();
    }
}
