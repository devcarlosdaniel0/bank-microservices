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
public class AccountProducer {

    private final KafkaTemplate<String, AccountSent> kafkaTemplate;

    public void sendAccount(AccountSent accountSent) {
        log.info("Producing for account created topic");
        Message<AccountSent> message = MessageBuilder
                .withPayload(accountSent)
                .setHeader(TOPIC, "account-created-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
