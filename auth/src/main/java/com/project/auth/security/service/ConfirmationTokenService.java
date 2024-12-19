package com.project.auth.security.service;

import com.project.auth.security.clients.EmailClient;
import com.project.auth.security.dto.EmailRequestDTO;
import com.project.auth.security.exception.AccountAlreadyConfirmedException;
import com.project.auth.security.exception.ConfirmationTokenExpiredException;
import com.project.auth.security.exception.ConfirmationTokenNotFoundException;
import com.project.auth.security.exception.EmailNotFoundException;
import com.project.core.domain.UserEntity;
import com.project.core.repository.UserEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmationTokenService {
    private final UserEntityRepository userEntityRepository;
    private final EmailClient emailClient;

    @Transactional
    public void createAndAssignConfirmationToken(UserEntity user) {
        final int EXPIRATION_TIME = 5;
        String confirmationToken = UUID.randomUUID().toString();

        user.setConfirmationToken(confirmationToken);
        user.setConfirmationTokenExpiration(LocalDateTime.now().plusMinutes(EXPIRATION_TIME));

        log.info("Creating & assigning confirmation token to user: {}", user.getEmail());
        userEntityRepository.save(user);

        sendConfirmationEmail(user);
    }

    @Transactional
    public UserEntity validateAndConsumeConfirmationToken(String token) {
        UserEntity user = userEntityRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new ConfirmationTokenNotFoundException("Confirmation token not found"));

        if (user.getConfirmationTokenExpiration() == null ||
                user.getConfirmationTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new ConfirmationTokenExpiredException("Confirmation token expired, try reconfirm");
        }

        user.setConfirmed(true);
        user.setConfirmationToken(null);
        user.setConfirmationTokenExpiration(null);
        userEntityRepository.save(user);

        return user;
    }

    @Transactional
    public void reconfirmEmail(EmailRequestDTO emailRequestDTO) {
        UserEntity user = userEntityRepository.findByEmail(emailRequestDTO.email())
                .orElseThrow(() -> new EmailNotFoundException("Email not found"));

        if (user.isConfirmed()) {
            throw new AccountAlreadyConfirmedException("Account already confirmed");
        }

        log.info("Resending confirmation token to user: {}", user.getEmail());
        createAndAssignConfirmationToken(user);
    }

    private void sendConfirmationEmail(UserEntity user) {
        String subject = "E-mail confirmation from Bank Project";
        String body = "Click on the link to confirm your e-mail: " +
                "http://localhost:8081/confirmEmail?token=" + user.getConfirmationToken();

        log.info("Sending email confirmation to: {}", user.getEmail());
        emailClient.sendEmail(user.getEmail(), subject, body);
    }

    @Transactional
    public void deleteUnconfirmedUsers() {
        List<UserEntity> unconfirmedUsers = userEntityRepository.
                findAllByIsConfirmedFalseAndConfirmationTokenExpirationBefore(LocalDateTime.now());

        if (!unconfirmedUsers.isEmpty()) {
            log.info("Deleting {} unconfirmed users...", unconfirmedUsers.size());
            userEntityRepository.deleteAll(unconfirmedUsers);
        }
    }

}
