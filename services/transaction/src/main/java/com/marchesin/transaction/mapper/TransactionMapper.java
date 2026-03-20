package com.marchesin.transaction.mapper;

import com.marchesin.transaction.domain.Transaction;
import com.marchesin.transaction.dto.TransactionResponse;
import com.marchesin.transaction.enums.TransactionDirection;
import com.marchesin.transaction.enums.TransactionType;
import com.marchesin.transaction.kafka.TransactionEvent;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse fromTransaction(Transaction transaction, String requestAccountId) {
        TransactionDirection direction = null;

        if (TransactionType.TRANSFER.equals(transaction.getType())) {
            direction = transaction.getSourceAccountId().equals(requestAccountId)
                    ? TransactionDirection.SENT
                    : TransactionDirection.RECEIVED;
        }

        return new TransactionResponse(
                transaction.getType(),
                direction,
                transaction.getSourceAmount(),
                transaction.getTargetAmount(),
                transaction.getSourceCurrency(),
                transaction.getTargetCurrency(),
                transaction.getExchangeRate(),
                transaction.getTargetEmail(),
                transaction.getTimeStamp()
        );
    }

    public Transaction fromEvent(TransactionEvent event) {
        return new Transaction(
                event.type(),
                event.sourceAmount(),
                event.targetAmount(),
                event.sourceCurrency(),
                event.targetCurrency(),
                event.exchangeRate(),
                event.sourceAccountId(),
                event.targetAccountId(),
                event.targetEmail(),
                event.timestamp());
    }
}
