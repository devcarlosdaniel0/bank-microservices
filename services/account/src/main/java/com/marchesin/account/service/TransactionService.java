package com.marchesin.account.service;

import com.marchesin.account.domain.Account;
import com.marchesin.account.domain.CurrencyCode;
import com.marchesin.account.dto.*;
import com.marchesin.account.dto.external.AuthenticatedUser;
import com.marchesin.account.dto.external.CurrencyResponse;
import com.marchesin.account.exception.AccountNotFound;
import com.marchesin.account.exception.SameAccountTransfer;
import com.marchesin.account.kafka.AccountProducer;
import com.marchesin.account.kafka.TransactionEvent;
import com.marchesin.account.kafka.enums.TransactionType;
import com.marchesin.account.kafka.factory.TransactionFactory;
import com.marchesin.account.repository.AccountRepository;
import com.marchesin.account.service.external.CurrencyConverterService;
import com.marchesin.account.service.external.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final AccountRepository repository;
    private final AccountProducer producer;
    private final CurrencyConverterService currencyConverterService;
    private final UserService userService;
    private final TransactionFactory factory;

    public TransactionService(AccountRepository repository, AccountProducer producer, CurrencyConverterService currencyConverterService, UserService userService, TransactionFactory factory) {
        this.repository = repository;
        this.producer = producer;
        this.currencyConverterService = currencyConverterService;
        this.userService = userService;
        this.factory = factory;
    }

    @Transactional
    public TransferResponse transfer(String userId, TransferRequest request) {
        Account from = getAccountFromUserId(userId);

        AuthenticatedUser userTo = userService.findByEmail(request.toEmail());

        Account to = getAccountFromUserId(userTo.id());

        if (from.getId().equals(to.getId())) {
            throw new SameAccountTransfer("Can not transfer to the same account");
        }

        BigDecimal debitAmount = request.amount();
        BigDecimal creditAmount;
        BigDecimal exchangeRate;

        if (from.getCurrencyCode().equals(to.getCurrencyCode())) {
            creditAmount = debitAmount;
            exchangeRate = null;
        } else {
            CurrencyResponse response = currencyConverterService.convert(
                    new CurrencyCode(from.getCurrencyCode()), new CurrencyCode(to.getCurrencyCode()), debitAmount);

            creditAmount = response.convertedAmount();
            exchangeRate = response.exchangeRate();
        }

        from.withdraw(debitAmount);
        to.deposit(creditAmount);

        producer.sendTransactionEvent(factory.createTransfer(from, to, debitAmount, creditAmount, exchangeRate, request.toEmail()));

        return new TransferResponse(from.getId(), debitAmount, from.getCurrencyCode(), request.toEmail(), creditAmount, to.getCurrencyCode());
    }

    @Transactional
    public BalanceResponse deposit(String userId, DepositRequest request) {
        Account account = getAccountFromUserId(userId);

        BigDecimal amount = request.amount();

        account.deposit(amount);

        producer.sendTransactionEvent(factory.createDeposit(account, amount));

        return new BalanceResponse(account.getBalanceAmount(), account.getCurrencyCode());
    }

    @Transactional
    public BalanceResponse withdraw(String userId, WithdrawRequest request) {
        Account account = getAccountFromUserId(userId);

        BigDecimal amount = request.amount();

        account.withdraw(amount);

        producer.sendTransactionEvent(factory.createWithdraw(account, amount));

        return new BalanceResponse(account.getBalanceAmount(), account.getCurrencyCode());
    }

    private Account getAccountFromUserId(String userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFound("Account not found"));
    }
}
