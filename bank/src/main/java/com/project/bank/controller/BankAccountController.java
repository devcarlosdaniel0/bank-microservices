package com.project.bank.controller;

import com.project.bank.dto.BankAccountResponseDTO;
import com.project.bank.dto.TransferDTO;
import com.project.bank.dto.UpdateBalanceDTO;
import com.project.bank.dto.UserFoundedDTO;
import com.project.bank.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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

    @GetMapping("findUserIdByUsername/{username}")
    public ResponseEntity<UserFoundedDTO> findUserIdByUsername(@PathVariable String username) {
        UserFoundedDTO userFounded = bankAccountService.findUserIdByUsername(username);
        return new ResponseEntity<>(userFounded, HttpStatus.OK);
    }

}
