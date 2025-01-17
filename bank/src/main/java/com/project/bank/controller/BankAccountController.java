package com.project.bank.controller;

import com.project.bank.dto.*;
import com.project.bank.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BankAccountController {
    private final BankAccountService bankAccountService;

    @PostMapping("create-bank-account")
    public ResponseEntity<BankAccountResponseDTO> createBankAccount(@RequestBody @Valid CreateBankAccountDTO createBankAccountDTO) {
        return new ResponseEntity<>(bankAccountService.createBankAccount(createBankAccountDTO), HttpStatus.CREATED);
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
    public ResponseEntity<List<BankAccountResponseDTO>> findAll() {
        return new ResponseEntity<>(bankAccountService.findAll(), HttpStatus.OK);
    }

    @PostMapping("transfer")
    public ResponseEntity<TransferResponseDTO> transfer(@RequestBody @Valid TransferDTO transferDTO) {
        return new ResponseEntity<>(bankAccountService.transfer(transferDTO), HttpStatus.OK);
    }

    @GetMapping("find-bank-account-id-by-account-email/{accountEmail}")
    public ResponseEntity<BankAccountFoundDTO> findBankAccountIdByAccountEmail(@PathVariable String accountEmail) {
        BankAccountFoundDTO bankAccountFounded = bankAccountService.findBankAccountIdByAccountEmail(accountEmail);
        return new ResponseEntity<>(bankAccountFounded, HttpStatus.OK);
    }
}
