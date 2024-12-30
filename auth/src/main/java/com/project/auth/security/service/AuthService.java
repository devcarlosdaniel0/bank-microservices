package com.project.auth.security.service;

import com.project.auth.security.dto.*;
import com.project.auth.security.exception.EmailAlreadyExistsException;
import com.project.auth.security.exception.EmailNotFoundException;
import com.project.auth.security.exception.InvalidPasswordException;
import com.project.core.domain.UserEntity;
import com.project.core.domain.UserRole;
import com.project.core.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenJwtService tokenJwtService;
    private final ConfirmationTokenService confirmationTokenService;

    @Transactional
    public TokenResponseDTO login(LoginDTO loginDTO) {
        userEntityRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new EmailNotFoundException(String.format("Email: '%s' not found", loginDTO.email())));

        try {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());

            Authentication auth = authenticationManager.authenticate(authentication);

            String jwtToken = tokenJwtService.generateToken((UserEntity) auth.getPrincipal());

            return new TokenResponseDTO(jwtToken);
        } catch (BadCredentialsException e) {
            throw new InvalidPasswordException("Invalid password!");
        }
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
                .roles(UserRole.USER)
                .build();

        userEntityRepository.save(userToBeSaved);

        confirmationTokenService.createAndAssignConfirmationToken(userToBeSaved);

        return new MessageResponseDTO("Register success! Please check your email to confirm your account");
    }

    @Transactional
    public MessageResponseDTO confirmEmail(String token) {
        UserEntity user = confirmationTokenService.validateAndConsumeConfirmationToken(token);

        log.info("Email confirmed successfully for user: {}", user.getEmail());
        return new MessageResponseDTO("Your account is confirmed, enjoy!");
    }

    @Transactional
    public MessageResponseDTO reconfirmEmail(EmailRequestDTO emailRequestDTO) {
        confirmationTokenService.reconfirmEmail(emailRequestDTO);

        log.info("Confirmation email token sent successfully to {}", emailRequestDTO.email());
        return new MessageResponseDTO("Check your email to confirm again your account!");
    }
}
