package com.marchesin.account.repository;

import com.marchesin.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByUserId(String userId);

    Optional<Account> findByUserId(String userId);
}
