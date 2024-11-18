package com.project.auth.security.controller;

import com.project.auth.security.dto.LoginDTO;
import com.project.auth.security.dto.RegisterDTO;
import com.project.auth.security.entity.UserEntity;
import com.project.auth.security.repository.UserEntityRepository;
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

    @Transactional
    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginDTO.username(), loginDTO.password());

        Authentication auth = authenticationManager.authenticate(authentication);

        return new ResponseEntity<>("Login Success!", HttpStatus.OK);
    }

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

        return new ResponseEntity<>("Register success!", HttpStatus.CREATED);
    }
}
