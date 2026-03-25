package com.marchesin.account.controller;

import com.marchesin.account.dto.AccountResponse;
import com.marchesin.account.dto.BalanceResponse;
import com.marchesin.account.dto.CreateAccountRequest;
import com.marchesin.account.dto.UpdateAccountRequest;
import com.marchesin.account.dto.external.AuthUser;
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
        AuthUser user = jwtUserMapper.from(jwt);

        return new ResponseEntity<>(service.createAccount(user, request), HttpStatus.CREATED);
    }

    @PutMapping("update")
    public ResponseEntity<AccountResponse> updateAccount(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid UpdateAccountRequest request
    ) {
        return new ResponseEntity<>(service.updateAccount(jwt.getSubject(), request), HttpStatus.OK);
    }

    @GetMapping("find-all")
    public ResponseEntity<Page<AccountResponse>> findAll(Pageable pageable) {

        return new ResponseEntity<>(service.findAll(pageable), HttpStatus.OK);
    }

    @DeleteMapping("delete")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt) {
        service.deleteAccount(jwt.getSubject());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("balance")
    public ResponseEntity<BalanceResponse> getBalance(@AuthenticationPrincipal Jwt jwt) {
        return new ResponseEntity<>(service.getBalance(jwt.getSubject()), HttpStatus.OK);
    }

    @GetMapping("find-by-user-id")
    public ResponseEntity<String> findAccountIdByUserId(
            @RequestParam String userId,
            @AuthenticationPrincipal Jwt jwt) {

        if (!jwt.getSubject().equals(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(service.getAccountIdByUserId(userId), HttpStatus.OK);
    }
}
