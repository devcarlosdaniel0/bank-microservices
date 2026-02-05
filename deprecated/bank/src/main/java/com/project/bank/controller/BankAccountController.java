package com.project.bank.controller;

import com.project.bank.dto.*;
import com.project.bank.service.BankAccountService;
import com.project.bank.service.TransactionService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BankAccountController {
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;

    @PostMapping("create-bank-account")
    public ResponseEntity<BankAccountResponseDTO> createBankAccount(@RequestBody @Valid CreateBankAccountDTO createBankAccountDTO) {
        return new ResponseEntity<>(bankAccountService.createBankAccount(createBankAccountDTO), HttpStatus.CREATED);
    }

    @GetMapping("check-balance")
    public ResponseEntity<BalanceResponseDTO> checkBalance() {
        return new ResponseEntity<>(bankAccountService.checkBalance(), HttpStatus.OK);
    }

    @GetMapping("hello-world")
    public String helloWorld() {
        return "Hello World";
    }

    @PostMapping("add-balance")
    public ResponseEntity<BankAccountResponseDTO> addBalance(@RequestBody @Valid UpdateBalanceDTO updateBalanceDTO) {
        return new ResponseEntity<>(bankAccountService.addBalance(updateBalanceDTO), HttpStatus.OK);
    }

    @PostMapping("withdrawal-balance")
    public ResponseEntity<BankAccountResponseDTO> withdrawalBalance(@RequestBody @Valid UpdateBalanceDTO updateBalanceDTO) {
        return new ResponseEntity<>(bankAccountService.withdrawalBalance(updateBalanceDTO), HttpStatus.OK);
    }

    @GetMapping("find-all")
    public ResponseEntity<Page<BankAccountResponseDTO>> findAll(Pageable pageable) {
        return new ResponseEntity<>(bankAccountService.findAll(pageable), HttpStatus.OK);
    }

    @PostMapping("transfer")
    public ResponseEntity<TransferResponseDTO> transfer(@RequestBody @Valid TransferDTO transferDTO) {
        return new ResponseEntity<>(transactionService.transfer(transferDTO), HttpStatus.OK);
    }

    @GetMapping("find-bank-account-id-by-account-email/{accountEmail}")
    public ResponseEntity<BankAccountFoundDTO> findBankAccountIdByAccountEmail(@PathVariable String accountEmail) {
        BankAccountFoundDTO bankAccountFounded = bankAccountService.findBankAccountIdByAccountEmail(accountEmail);
        return new ResponseEntity<>(bankAccountFounded, HttpStatus.OK);
    }
}
