package com.marchesin.account.controller;

import com.marchesin.account.dto.*;
import com.marchesin.account.dto.external.AuthUser;
import com.marchesin.account.mapper.JwtUserMapper;
import com.marchesin.account.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final TransactionService service;
    private final JwtUserMapper jwtUserMapper;

    public TransactionController(TransactionService service, JwtUserMapper jwtUserMapper) {
        this.service = service;
        this.jwtUserMapper = jwtUserMapper;
    }

    @PostMapping("deposit")
    public ResponseEntity<BalanceResponse> deposit(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid DepositRequest request
    ) {
        return new ResponseEntity<>(service.deposit(jwt.getSubject(), request), HttpStatus.OK);
    }

    @PostMapping("withdraw")
    public ResponseEntity<BalanceResponse> withdraw(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid WithdrawRequest request
    ) {
        return new ResponseEntity<>(service.withdraw(jwt.getSubject(), request), HttpStatus.OK);
    }

    @PostMapping("transfer")
    public ResponseEntity<TransferResponse> transfer(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid TransferRequest request
    ) {
        AuthUser user = jwtUserMapper.from(jwt);

        return new ResponseEntity<>(service.transfer(user, request), HttpStatus.OK);
    }
}
