package com.project.bank.repository;

import com.project.bank.domain.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    Optional<BankAccount> findByAccountEmail(String accountEmail);

    Optional<BankAccount> findByKeycloakUserId(String keycloakUserId);
}
