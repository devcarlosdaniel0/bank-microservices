package com.project.bank.controller;

import com.project.bank.dto.CreateBankAccountDTO;
import com.project.bank.service.BankAccountService;
import com.project.core.domain.BankAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BankAccountController {
    private final BankAccountService bankAccountService;

    @PostMapping("create")
    public ResponseEntity<BankAccount> createBankAccount(@RequestBody CreateBankAccountDTO createBankAccountDTO) {
        return new ResponseEntity<>(bankAccountService.createBankAccount(createBankAccountDTO), HttpStatus.CREATED);
    }
}
