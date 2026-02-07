package com.marchesin.user.controller;

import com.marchesin.user.domain.AuthenticatedUser;
import com.marchesin.user.service.IdentityProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final IdentityProvider identityProvider;

    public UserController(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    @GetMapping
    public ResponseEntity<AuthenticatedUser> findByEmail(@RequestParam String email) {
        return new ResponseEntity<>(identityProvider.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)),
                HttpStatus.OK);
    }
}
