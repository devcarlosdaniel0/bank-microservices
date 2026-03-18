package com.marchesin.transaction.kafka;

import com.marchesin.transaction.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransactionConsumer {

    private final TransactionService service;

    public TransactionConsumer(TransactionService service) {
        this.service = service;
    }

    @KafkaListener(topics = "transaction-created", groupId = "transaction-group")
    public void consumeTransactionCreated(TransactionEvent event) {
        log.info("Consuming 'account created topic' for account ID: {}", event.sourceAccountId());

        service.createTransaction(event);
    }
}
