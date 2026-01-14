package com.marchesin.account.controller;

import com.marchesin.account.dto.AccountResponse;
import com.marchesin.account.dto.AuthenticatedUser;
import com.marchesin.account.dto.CreateAccountRequest;
import com.marchesin.account.mapper.JwtUserMapper;
import com.marchesin.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService service;
    private final JwtUserMapper jwtUserMapper;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid CreateAccountRequest request
    ) {

        AuthenticatedUser user = jwtUserMapper.from(jwt);

        return new ResponseEntity<>(service.createAccount(user, request), HttpStatus.CREATED);
    }
}
