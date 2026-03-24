package com.marchesin.account.service;

import com.marchesin.account.domain.Account;
import com.marchesin.account.domain.CurrencyCode;
import com.marchesin.account.dto.*;
import com.marchesin.account.dto.external.AuthUser;
import com.marchesin.account.dto.external.CurrencyResponse;
import com.marchesin.account.exception.AccountNotFound;
import com.marchesin.account.exception.SameAccountTransfer;
import com.marchesin.account.kafka.AccountProducer;
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
    public TransferResponse transfer(AuthUser sourceUser, TransferRequest request) {
        Account source = getAccountFromUserId(sourceUser.id());

        AuthUser targetUser = userService.findByEmail(request.toEmail());

        Account target = getAccountFromUserId(targetUser.id());

        if (source.getId().equals(target.getId())) {
            throw new SameAccountTransfer("Can not transfer to the same account");
        }

        BigDecimal debitAmount = request.amount();
        BigDecimal creditAmount;
        BigDecimal exchangeRate;

        if (source.getCurrencyCode().equals(target.getCurrencyCode())) {
            creditAmount = debitAmount;
            exchangeRate = null;
        } else {
            CurrencyResponse response = currencyConverterService.convert(
                    new CurrencyCode(source.getCurrencyCode()), new CurrencyCode(target.getCurrencyCode()), debitAmount);

            creditAmount = response.convertedAmount();
            exchangeRate = response.exchangeRate();
        }

        source.withdraw(debitAmount);
        target.deposit(creditAmount);

        producer.sendTransactionEvent(factory.createTransfer(source, target, debitAmount, creditAmount, exchangeRate, sourceUser.email(), request.toEmail()));

        return new TransferResponse(source.getId(), debitAmount, source.getCurrencyCode(), request.toEmail(), creditAmount, target.getCurrencyCode());
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
