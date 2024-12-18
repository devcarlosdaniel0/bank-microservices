package com.project.auth.security.service;

import com.project.auth.security.clients.EmailClient;
import com.project.auth.security.dto.LoginDTO;
import com.project.auth.security.dto.MessageResponseDTO;
import com.project.auth.security.dto.RegisterDTO;
import com.project.auth.security.dto.TokenResponseDTO;
import com.project.auth.security.exception.EmailAlreadyExistsException;
import com.project.core.domain.UserEntity;
import com.project.core.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailClient emailClient;

    @Transactional
    public TokenResponseDTO login(LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());

        Authentication auth = authenticationManager.authenticate(authentication);

        String token = tokenService.generateToken((UserEntity) auth.getPrincipal());

        return new TokenResponseDTO(token);
    }

    @Transactional
    public MessageResponseDTO register(RegisterDTO registerDTO) {
        if (userEntityRepository.findByEmail(registerDTO.email()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }

        UserEntity userToBeSaved = UserEntity.builder()
                .email(registerDTO.email())
                .username(registerDTO.username())
                .password(passwordEncoder.encode(registerDTO.password()))
                .roles(registerDTO.role())
                .confirmationToken(UUID.randomUUID().toString())
                .build();

        log.info("Saving user to be saved");
        userEntityRepository.save(userToBeSaved);

        log.info("Calling method send confirmation email");
        sendConfirmationEmail(userToBeSaved);

        return new MessageResponseDTO("Register success! Please check your email to confirm your account");
    }

    @Transactional
    public MessageResponseDTO confirmEmail(String token) {
        UserEntity user = userEntityRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        user.setConfirmed(true);
        user.setConfirmationToken(null);
        userEntityRepository.save(user);
        log.info("Email confirmed successfully");

        return new MessageResponseDTO("Your account it's confirmed, enjoy!");
    }

    private void sendConfirmationEmail(UserEntity user) {
        String subject = "E-mail confirmation from Bank Project";
        String body = "Click on the link to confirm your e-mail: " +
                "http://localhost:8081/confirm?token=" + user.getConfirmationToken();

        log.info("Sending email confirmation to: {}", user.getEmail());
        emailClient.sendEmail(user.getEmail(), subject, body);
    }
}
