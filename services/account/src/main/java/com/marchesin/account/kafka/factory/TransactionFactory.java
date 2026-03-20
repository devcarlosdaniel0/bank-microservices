package com.marchesin.account.kafka.factory;

import com.marchesin.account.domain.Account;
import com.marchesin.account.kafka.TransactionEvent;
import com.marchesin.account.kafka.enums.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class TransactionFactory {

    public TransactionEvent createDeposit(Account account, BigDecimal amount){
        return new TransactionEvent(
                TransactionType.DEPOSIT,
                amount,
                null,
                account.getCurrencyCode(),
                null,
                null,
                account.getId(),
                null,
                null,
                LocalDateTime.now());
    }

    public TransactionEvent createWithdraw(Account account, BigDecimal amount){
        return new TransactionEvent(
                TransactionType.WITHDRAW,
                amount,
                null,
                account.getCurrencyCode(),
                null,
                null,
                account.getId(),
                null,
                null,
                LocalDateTime.now());
    }

    public TransactionEvent createTransfer(Account source, Account target, BigDecimal sourceAmount, BigDecimal targetAmount, BigDecimal exchangeRate, String targetEmail){
        return new TransactionEvent(
                TransactionType.TRANSFER,
                sourceAmount,
                targetAmount,
                source.getCurrencyCode(),
                target.getCurrencyCode(),
                exchangeRate,
                source.getId(),
                target.getId(),
                targetEmail,
                LocalDateTime.now());
    }

    public TransactionEvent createExchange(Account account, BigDecimal oldBalance, BigDecimal newBalance, String oldCurrency, String newCurrency, BigDecimal exchangeRate){
        return new TransactionEvent(
                TransactionType.EXCHANGE,
                oldBalance,
                newBalance,
                oldCurrency,
                newCurrency,
                exchangeRate,
                account.getId(),
                null,
                null,
                LocalDateTime.now());
    }
}
