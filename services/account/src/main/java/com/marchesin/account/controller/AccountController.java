package com.marchesin.account.controller;

import com.marchesin.account.dto.*;
import com.marchesin.account.mapper.JwtUserMapper;
import com.marchesin.account.service.AccountService;
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

    private final AccountService service;
    private final JwtUserMapper jwtUserMapper;

    public AccountController(AccountService service, JwtUserMapper jwtUserMapper) {
        this.service = service;
        this.jwtUserMapper = jwtUserMapper;
    }

    @PostMapping("create")
    public ResponseEntity<AccountResponse> createAccount(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid CreateAccountRequest request
    ) {
        AuthenticatedUser user = jwtUserMapper.from(jwt);

        return new ResponseEntity<>(service.createAccount(user, request), HttpStatus.CREATED);
    }

    @PutMapping("update")
    public ResponseEntity<AccountResponse> updateAccount(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid UpdateAccountRequest request
    ) {
        AuthenticatedUser user = jwtUserMapper.from(jwt);

        return new ResponseEntity<>(service.updateAccount(user, request), HttpStatus.OK);
    }

    @GetMapping("find-all")
    public ResponseEntity<Page<AccountResponse>> findAll(Pageable pageable) {

        return new ResponseEntity<>(service.findAll(pageable), HttpStatus.OK);
    }

    @DeleteMapping("delete")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Jwt jwt
    ) {
        AuthenticatedUser user = jwtUserMapper.from(jwt);

        service.deleteAccount(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("balance")
    public ResponseEntity<BalanceResponse> getBalance(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return new ResponseEntity<>(service.getBalance(jwt.getSubject()), HttpStatus.OK);
    }

//    @PostMapping("deposit")
//    public ResponseEntity<BalanceResponse> deposit(
//            @AuthenticationPrincipal Jwt jwt,
//            @RequestBody @Valid DepositRequest request
//    ) {
//        return new ResponseEntity<>(service.deposit(jwt.getSubject(), request), HttpStatus.OK);
//    }
//
//    @PostMapping("withdraw")
//    public ResponseEntity<BalanceResponse> withdraw(
//            @AuthenticationPrincipal Jwt jwt,
//            @RequestBody @Valid WithdrawalRequest request
//    ) {
//        return new ResponseEntity<>(service.withdraw(jwt.getSubject(), request), HttpStatus.OK);
//    }
//
//    @PostMapping("transfer")
//    public ResponseEntity<TransferResponse> transfer(
//            @AuthenticationPrincipal Jwt jwt,
//            @RequestBody @Valid TransferRequest request
//    ) {
//        return new ResponseEntity<>(service.transfer(jwt.getSubject(), request), HttpStatus.OK);
//    }
}
