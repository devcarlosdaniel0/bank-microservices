package com.project.auth.security.controller;

import com.project.auth.security.dto.RegisterDTO;
import com.project.auth.security.entity.UserEntity;
import com.project.auth.security.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO) {
        if (userEntityRepository.findByUsername(registerDTO.username()).isPresent()) {
            return new ResponseEntity<>("Username already exists!", HttpStatus.BAD_REQUEST);
        }

        UserEntity userToBeSaved = UserEntity.builder()
                .username(registerDTO.username())
                .password(passwordEncoder.encode(registerDTO.password()))
                .roles(registerDTO.role())
                .build();

        userEntityRepository.save(userToBeSaved);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
