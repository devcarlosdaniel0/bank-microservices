package com.project.auth.security.controller;

import com.project.auth.security.dto.*;
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

    @GetMapping("confirmEmail")
    public ResponseEntity<MessageResponseDTO> confirmEmail(@RequestParam String token) {
        return new ResponseEntity<>(authService.confirmEmail(token), HttpStatus.OK);
    }

    @PostMapping("reconfirmEmail")
    public ResponseEntity<MessageResponseDTO> reconfirmEmail(@RequestBody @Valid EmailRequestDTO emailRequestDTO) {
        return new ResponseEntity<>(authService.reconfirmEmail(emailRequestDTO), HttpStatus.OK);
    }

}
