package com.marchesin.balance.controlller;

import com.marchesin.balance.dto.BalanceResponse;
import com.marchesin.balance.dto.DepositRequest;
import com.marchesin.balance.dto.WithdrawalRequest;
import com.marchesin.balance.service.BalanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/balance")
public class BalanceController {
    private final BalanceService service;

    public BalanceController(BalanceService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<BalanceResponse> getBalance(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return new ResponseEntity<>(service.getBalance(jwt.getSubject()), HttpStatus.OK);
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
            @RequestBody @Valid WithdrawalRequest request
            ) {
        return new ResponseEntity<>(service.withdraw(jwt.getSubject(), request), HttpStatus.OK);
    }
}
