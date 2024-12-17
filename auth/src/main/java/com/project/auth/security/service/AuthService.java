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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
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

        userEntityRepository.save(userToBeSaved);

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

        return new MessageResponseDTO("Your account it's confirmed, enjoy!");
    }

    private void sendConfirmationEmail(UserEntity user) {
        String subject = "E-mail confirmation from Bank Project";
        String body = "Click on the link to confirm your e-mail: " +
                "http://localhost:8081/confirm?token=" + user.getConfirmationToken();
        emailClient.sendEmail(user.getEmail(), subject, body);
    }
}
