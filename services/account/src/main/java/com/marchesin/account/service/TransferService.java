package com.marchesin.account.service;

import com.marchesin.account.domain.Account;
import com.marchesin.account.domain.CurrencyCode;
import com.marchesin.account.dto.TransferRequest;
import com.marchesin.account.dto.TransferResponse;
import com.marchesin.account.enums.TransactionType;
import com.marchesin.account.exception.AccountNotFound;
import com.marchesin.account.exception.SameAccountTransfer;
import com.marchesin.account.kafka.AccountProducer;
import com.marchesin.account.kafka.TransactionEvent;
import com.marchesin.account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransferService {

    private final AccountRepository repository;
    private final AccountProducer producer;
    private final CurrencyConversionService conversionService;

    public TransferService(AccountRepository repository, AccountProducer producer, CurrencyConversionService conversionService) {
        this.repository = repository;
        this.producer = producer;
        this.conversionService = conversionService;
    }

    @Transactional
    public TransferResponse transfer(String userId, TransferRequest request) {
        Account from = getAccountFromUserId(userId);
        Account to = getAccountFromId(request.toAccountId());

        if (from.getId().equals(to.getId())) {
            throw new SameAccountTransfer("Cannot transfer to the same account");
        }

        BigDecimal debitAmount = request.amount();
        BigDecimal creditAmount = conversionService.convert(new CurrencyCode(from.getCurrencyCode()), new CurrencyCode(to.getCurrencyCode()), debitAmount);

        from.withdraw(debitAmount);
        to.deposit(creditAmount);

        TransactionEvent eventFrom = new TransactionEvent(from.getId(), TransactionType.TRANSFER_OUT, debitAmount, from.getCurrencyCode(), LocalDateTime.now());
        TransactionEvent eventTo = new TransactionEvent(to.getId(), TransactionType.TRANSFER_IN, creditAmount, to.getCurrencyCode(), LocalDateTime.now());

        producer.sendTransactionEvent(eventFrom);
        producer.sendTransactionEvent(eventTo);

        return new TransferResponse(from.getId(), debitAmount, from.getCurrencyCode(), to.getId(), creditAmount, to.getCurrencyCode());
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
