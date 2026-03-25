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
        return TransactionEvent.builder()
                .type(TransactionType.DEPOSIT)
                .sourceAmount(amount)
                .sourceCurrency(account.getCurrencyCode())
                .sourceAccountId(account.getId())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public TransactionEvent createWithdraw(Account account, BigDecimal amount){
        return TransactionEvent.builder()
                .type(TransactionType.WITHDRAW)
                .sourceAmount(amount)
                .sourceCurrency(account.getCurrencyCode())
                .sourceAccountId(account.getId())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public TransactionEvent createTransfer(Account source, Account target, BigDecimal sourceAmount, BigDecimal targetAmount, BigDecimal exchangeRate, String sourceEmail, String targetEmail){
        return new TransactionEvent(
                TransactionType.TRANSFER,
                sourceAmount,
                targetAmount,
                source.getCurrencyCode(),
                target.getCurrencyCode(),
                exchangeRate,
                source.getId(),
                target.getId(),
                sourceEmail,
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
                null,
                LocalDateTime.now());
    }
}
