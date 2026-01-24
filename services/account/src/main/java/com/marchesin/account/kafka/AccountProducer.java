package com.marchesin.account.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AccountProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAccountCreated(AccountTopicSent accountTopicSent) {
        send("account-created-topic", accountTopicSent);
    }

    public void sendAccountDeleted(String accountId) {
        send("account-deleted-topic", accountId);
    }

    public void sendAccountUpdated(AccountTopicSent accountTopicSent) {
        send("account-updated-topic", accountTopicSent);
    }

    private void send(String topic, Object payload) {
        log.info("Producing to topic {}: {}", topic, payload);
        Message<Object> message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        kafkaTemplate.send(message);
    }
}
