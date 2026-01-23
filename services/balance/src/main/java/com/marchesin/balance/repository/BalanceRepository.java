package com.marchesin.balance.repository;

import com.marchesin.balance.domain.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, String> {

    Optional<Balance> findByAccountId(String accountId);
    Optional<Balance> findByUserId(String userId);
    boolean existsByAccountId(String accountId);
    void deleteByAccountId(String accountId);
}
