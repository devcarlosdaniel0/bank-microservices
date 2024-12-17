package com.project.auth.security.controller;

import com.project.auth.security.dto.LoginDTO;
import com.project.auth.security.dto.MessageResponseDTO;
import com.project.auth.security.dto.RegisterDTO;
import com.project.auth.security.dto.TokenResponseDTO;
import com.project.auth.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginDTO loginDTO) {
        return new ResponseEntity<>(authService.login(loginDTO), HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<MessageResponseDTO> register(@RequestBody @Valid RegisterDTO registerDTO) {
        return new ResponseEntity<>(authService.register(registerDTO), HttpStatus.CREATED);
    }

    @GetMapping("confirm")
    public ResponseEntity<MessageResponseDTO> confirmEmail(@RequestParam String token) {
        return ResponseEntity.ok(authService.confirmEmail(token));
    }
}
