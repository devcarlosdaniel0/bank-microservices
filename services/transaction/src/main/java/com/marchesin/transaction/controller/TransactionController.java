package com.marchesin.transaction.controller;

import com.marchesin.transaction.client.AccountServiceClient;
import com.marchesin.transaction.domain.Transaction;
import com.marchesin.transaction.dto.TransactionResponse;
import com.marchesin.transaction.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/transaction")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping("me")
    public ResponseEntity<List<TransactionResponse>> findAllByAccountId(@AuthenticationPrincipal Jwt jwt) {
        return new ResponseEntity<>(service.findAllTransactions(jwt.getSubject()), HttpStatus.OK);
    }
}
