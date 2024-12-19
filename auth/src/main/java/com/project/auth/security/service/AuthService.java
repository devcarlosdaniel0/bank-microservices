package com.project.auth.security.service;

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
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());

        Authentication auth = authenticationManager.authenticate(authentication);

        String token = tokenJwtService.generateToken((UserEntity) auth.getPrincipal());

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
}
