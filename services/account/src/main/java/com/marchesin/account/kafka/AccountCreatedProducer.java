package com.marchesin.account.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountCreatedProducer {

    private final KafkaTemplate<String, AccountCreated> kafkaTemplate;

    public void sendAccountCreated(AccountCreated accountCreated) {
        log.info("Producing account created topic");
        Message<AccountCreated> message = MessageBuilder
                .withPayload(accountCreated)
                .setHeader(TOPIC, "account-created-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
