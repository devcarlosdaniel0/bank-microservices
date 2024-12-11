package com.project.auth.security.controller;

import com.project.auth.security.dto.LoginDTO;
import com.project.auth.security.dto.RegisterDTO;
import com.project.core.domain.UserEntity;
import com.project.core.repository.UserEntityRepository;
import com.project.auth.security.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional
    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());

        Authentication auth = authenticationManager.authenticate(authentication);

        String token = tokenService.generateToken((UserEntity) auth.getPrincipal());

        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDTO registerDTO) {
        if (userEntityRepository.findByEmail(registerDTO.email()).isPresent()) {
            return new ResponseEntity<>("Email already exists!", HttpStatus.BAD_REQUEST);
        }

        UserEntity userToBeSaved = UserEntity.builder()
                .email(registerDTO.email())
                .username(registerDTO.username())
                .password(passwordEncoder.encode(registerDTO.password()))
                .roles(registerDTO.role())
                .build();

        userEntityRepository.save(userToBeSaved);

        return new ResponseEntity<>("Register success!", HttpStatus.CREATED);
    }
}
