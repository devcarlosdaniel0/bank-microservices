package com.marchesin.balance.kafka;

import com.marchesin.balance.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceConsumer {

    private final BalanceService balanceService;

    @KafkaListener(topics = "account-created-topic", groupId = "balance-group")
    public void consumeAccountCreated(Account account) {
        log.info("Consuming account created topic for account ID: {}", account.accountId());

        balanceService.createInitialBalance(account);
    }

} 