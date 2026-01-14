package com.marchesin.account.service;

import com.marchesin.account.domain.Account;
import com.marchesin.account.dto.AccountResponse;
import com.marchesin.account.dto.CreateAccountRequest;
import com.marchesin.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Currency;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;

    public AccountResponse createAccount(Jwt jwt, CreateAccountRequest request) {
        String userId = jwt.getSubject();
        String email = jwt.getClaim("email");
        String ownerName = jwt.getClaim("name");

        Currency currency = request.currency();

        if (repository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("User already has a account");
        }

        Account account = Account.builder()
                .userId(userId)
                .ownerName(ownerName)
                .email(email)
                .currency(currency)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(account);

        return new AccountResponse(
                account.getId(),
                account.getUserId(),
                account.getOwnerName(),
                account.getEmail(),
                account.getCurrency(),
                account.getCreatedAt()
        );
    }
}
