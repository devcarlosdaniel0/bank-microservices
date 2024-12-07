package com.project.bank.controller;

import com.project.bank.dto.UpdateBalanceDTO;
import com.project.bank.dto.BankAccountResponseDTO;
import com.project.bank.dto.CreateBankAccountDTO;
import com.project.bank.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BankAccountController {
    private final BankAccountService bankAccountService;

    @PostMapping("create")
    public ResponseEntity<BankAccountResponseDTO> createBankAccount(@RequestBody @Valid CreateBankAccountDTO createBankAccountDTO) {
        return new ResponseEntity<>(bankAccountService.createBankAccount(createBankAccountDTO), HttpStatus.CREATED);
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
}
