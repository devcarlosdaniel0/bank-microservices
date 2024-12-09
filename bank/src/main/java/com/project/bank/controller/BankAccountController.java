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

    @PostMapping("create")
    public ResponseEntity<BankAccountResponseDTO> createBankAccount() {
        return new ResponseEntity<>(bankAccountService.createBankAccount(), HttpStatus.CREATED);
    }

    @PostMapping("addBalance")
    public ResponseEntity<BankAccountResponseDTO> addBalance(@RequestBody @Valid UpdateBalanceDTO updateBalanceDTO) {
        return new ResponseEntity<>(bankAccountService.addBalance(updateBalanceDTO), HttpStatus.OK);
    }

    @PostMapping("withdrawalBalance")
    public ResponseEntity<BankAccountResponseDTO> withdrawalBalance(@RequestBody @Valid UpdateBalanceDTO updateBalanceDTO) {
        return new ResponseEntity<>(bankAccountService.withdrawalBalance(updateBalanceDTO), HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<List<BankAccountResponseDTO>> findAll() {
        return new ResponseEntity<>(bankAccountService.findAll(), HttpStatus.OK);
    }

    @PostMapping("transfer")
    public ResponseEntity<TransferResponseDTO> transfer(@RequestBody @Valid TransferDTO transferDTO) {
        return new ResponseEntity<>(bankAccountService.transfer(transferDTO), HttpStatus.OK);
    }

    @GetMapping("findBankAccountIdByAccountName/{accountName}")
    public ResponseEntity<BankAccountFoundedDTO> findBankAccountIdByAccountName(@PathVariable String accountName) {
        BankAccountFoundedDTO bankAccountFounded = bankAccountService.findBankAccountIdByAccountName(accountName);
        return new ResponseEntity<>(bankAccountFounded, HttpStatus.OK);
    }

    @GetMapping("findUserIdByUsername/{username}")
    public ResponseEntity<UserFoundedDTO> findUserIdByUsername(@PathVariable String username) {
        UserFoundedDTO userFounded = bankAccountService.findUserIdByUsername(username);
        return new ResponseEntity<>(userFounded, HttpStatus.OK);
    }
}
