package com.marchesin.balance.controlller;

import com.marchesin.balance.dto.BalanceResponse;
import com.marchesin.balance.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/balance")
public class BalanceController {
    private final BalanceService service;

    @GetMapping
    public ResponseEntity<BalanceResponse> getBalance(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return new ResponseEntity<>(service.getBalance(jwt.getSubject()), HttpStatus.OK);
    }
}
