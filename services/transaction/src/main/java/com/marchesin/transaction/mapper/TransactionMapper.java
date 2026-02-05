package com.marchesin.transaction.mapper;

import com.marchesin.transaction.domain.Transaction;
import com.marchesin.transaction.dto.TransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse fromTransaction(Transaction transaction) {

        return new TransactionResponse(
                transaction.getAmount(),
                transaction.getCurrencyCode(),
                transaction.getType(),
                transaction.getCreatedAt());
    }
}
