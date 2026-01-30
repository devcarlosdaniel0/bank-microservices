package com.marchesin.transaction.controller;

import com.marchesin.transaction.domain.Transaction;
import com.marchesin.transaction.dto.TransactionResponse;
import com.marchesin.transaction.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/transaction")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping("{accountId}")
    public ResponseEntity<List<TransactionResponse>> findAllByAccountId(
            @PathVariable String accountId) {

        return new ResponseEntity<>(service.findAllTransactions(accountId), HttpStatus.OK);
    }
}
