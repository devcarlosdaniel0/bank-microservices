package com.project.auth.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasksService {
    private final ConfirmationTokenService confirmationTokenService;

    @Scheduled(cron = "0 0/30 * * * ?") // Executa a cada 30 minutos
    public void cleanUpUnconfirmedUsers() {
        log.info("Running scheduled task to clean up unconfirmed users...");
        confirmationTokenService.deleteUnconfirmedUsers();
    }
}