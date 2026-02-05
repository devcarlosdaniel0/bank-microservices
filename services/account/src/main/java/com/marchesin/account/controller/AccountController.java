package com.marchesin.account.controller;

import com.marchesin.account.dto.*;
import com.marchesin.account.mapper.JwtUserMapper;
import com.marchesin.account.service.AccountService;
import com.marchesin.account.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final JwtUserMapper jwtUserMapper;

    public AccountController(AccountService accountService, TransactionService transactionService, JwtUserMapper jwtUserMapper) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.jwtUserMapper = jwtUserMapper;
    }

    @PostMapping("create")
    public ResponseEntity<AccountResponse> createAccount(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid CreateAccountRequest request
    ) {
        AuthenticatedUser user = jwtUserMapper.from(jwt);

        return new ResponseEntity<>(accountService.createAccount(user, request), HttpStatus.CREATED);
    }

    @PutMapping("update")
    public ResponseEntity<AccountResponse> updateAccount(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid UpdateAccountRequest request
    ) {
        return new ResponseEntity<>(accountService.updateAccount(jwt.getSubject(), request), HttpStatus.OK);
    }

    @GetMapping("find-all")
    public ResponseEntity<Page<AccountResponse>> findAll(Pageable pageable) {

        return new ResponseEntity<>(accountService.findAll(pageable), HttpStatus.OK);
    }

    @DeleteMapping("delete")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt) {
        accountService.deleteAccount(jwt.getSubject());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("balance")
    public ResponseEntity<BalanceResponse> getBalance(@AuthenticationPrincipal Jwt jwt) {
        return new ResponseEntity<>(accountService.getBalance(jwt.getSubject()), HttpStatus.OK);
    }

    @PostMapping("deposit")
    public ResponseEntity<BalanceResponse> deposit(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid DepositRequest request
    ) {
        return new ResponseEntity<>(transactionService.deposit(jwt.getSubject(), request), HttpStatus.OK);
    }

    @PostMapping("withdraw")
    public ResponseEntity<BalanceResponse> withdraw(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid WithdrawRequest request
    ) {
        return new ResponseEntity<>(transactionService.withdraw(jwt.getSubject(), request), HttpStatus.OK);
    }

    @PostMapping("transfer")
    public ResponseEntity<TransferResponse> transfer(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid TransferRequest request
    ) {
        return new ResponseEntity<>(transactionService.transfer(jwt.getSubject(), request), HttpStatus.OK);
    }
}
