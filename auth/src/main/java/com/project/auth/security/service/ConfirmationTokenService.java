package com.project.auth.security.service;

import com.project.auth.security.clients.EmailClient;
import com.project.core.domain.UserEntity;
import com.project.core.repository.UserEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmationTokenService {
    private final UserEntityRepository userEntityRepository;
    private final EmailClient emailClient;

    public void createAndAssignConfirmationToken(UserEntity user) {
        String confirmationToken = UUID.randomUUID().toString();

        user.setConfirmationToken(confirmationToken);
        user.setConfirmationTokenExpiration(LocalDateTime.now().plusMinutes(1));

        log.info("Creating & assigning confirmation token to user: {}", user.getEmail());
        userEntityRepository.save(user);

        sendConfirmationEmail(user);
    }

    @Transactional
    public UserEntity validateAndConsumeConfirmationToken(String token) {
        UserEntity user = userEntityRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (user.getConfirmationTokenExpiration() == null ||
                user.getConfirmationTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        user.setConfirmed(true);
        user.setConfirmationToken(null);
        user.setConfirmationTokenExpiration(null);
        userEntityRepository.save(user);

        return user;
    }

    private void sendConfirmationEmail(UserEntity user) {
        String subject = "E-mail confirmation from Bank Project";
        String body = "Click on the link to confirm your e-mail: " +
                "http://localhost:8081/confirm?token=" + user.getConfirmationToken();

        log.info("Sending email confirmation to: {}", user.getEmail());
        emailClient.sendEmail(user.getEmail(), subject, body);
    }
}
