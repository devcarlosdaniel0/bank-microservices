package com.marchesin.account.service;

import com.marchesin.account.domain.Account;
import com.marchesin.account.domain.CurrencyCode;
import com.marchesin.account.dto.*;
import com.marchesin.account.dto.external.AuthenticatedUser;
import com.marchesin.account.exception.AccountNotFound;
import com.marchesin.account.exception.SameAccountTransfer;
import com.marchesin.account.kafka.AccountProducer;
import com.marchesin.account.kafka.enums.TransactionType;
import com.marchesin.account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final AccountRepository repository;
    private final AccountProducer producer;
    private final CurrencyConverterService currencyConverterService;
    private final UserService userService;

    public TransactionService(AccountRepository repository, AccountProducer producer, CurrencyConverterService currencyConverterService, UserService userService) {
        this.repository = repository;
        this.producer = producer;
        this.currencyConverterService = currencyConverterService;
        this.userService = userService;
    }

    @Transactional
    public TransferResponse transfer(String userId, TransferRequest request) {
        Account from = getAccountFromUserId(userId);

        AuthenticatedUser userTo = userService.findByEmail(request.toEmail());

        Account to = getAccountFromUserId(userTo.id());

        if (from.getId().equals(to.getId())) {
            throw new SameAccountTransfer("Cannot transfer to the same account");
        }

        BigDecimal debitAmount = request.amount();
        BigDecimal creditAmount = currencyConverterService.convert(new CurrencyCode(from.getCurrencyCode()), new CurrencyCode(to.getCurrencyCode()), debitAmount);

        from.withdraw(debitAmount);
        to.deposit(creditAmount);

        producer.sendTransactionEvent(TransactionType.TRANSFER_OUT.create(from.getId(), debitAmount, from.getCurrencyCode()));
        producer.sendTransactionEvent(TransactionType.TRANSFER_IN.create(to.getId(), creditAmount, to.getCurrencyCode()));

        return new TransferResponse(from.getId(), debitAmount, from.getCurrencyCode(), request.toEmail(), creditAmount, to.getCurrencyCode());
    }

    @Transactional
    public BalanceResponse deposit(String userId, DepositRequest request) {
        Account account = getAccountFromUserId(userId);

        account.deposit(request.amount());

        producer.sendTransactionEvent(TransactionType.DEPOSIT.create(account.getId(), request.amount(), account.getCurrencyCode()));

        return new BalanceResponse(account.getBalanceAmount(), account.getCurrencyCode());
    }

    @Transactional
    public BalanceResponse withdraw(String userId, WithdrawRequest request) {
        Account account = getAccountFromUserId(userId);

        account.withdraw(request.amount());

        producer.sendTransactionEvent(TransactionType.WITHDRAW.create(account.getId(), request.amount(), account.getCurrencyCode()));

        return new BalanceResponse(account.getBalanceAmount(), account.getCurrencyCode());
    }

    private Account getAccountFromUserId(String userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFound("Account not found"));
    }
}
