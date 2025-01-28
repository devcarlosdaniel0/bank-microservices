package com.project.bank.controller;

import com.project.bank.dto.*;
import com.project.bank.service.BankAccountService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BankAccountController {
    @PostMapping("create-bank-account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bank account created"),
            @ApiResponse(responseCode = "404", description = "User id from JWT token not found"),
            @ApiResponse(responseCode = "409", description = "Bank account already exists"),
            @ApiResponse(responseCode = "422", description = "Invalid currency code"),
            @ApiResponse(responseCode = "403", description = "User not confirmed"),
    })
    public ResponseEntity<BankAccountResponseDTO> createBankAccount(@RequestBody @Valid CreateBankAccountDTO createBankAccountDTO) {
        return new ResponseEntity<>(bankAccountService.createBankAccount(createBankAccountDTO), HttpStatus.CREATED);
    }

    private final BankAccountService bankAccountService;

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
