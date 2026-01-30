package com.marchesin.transaction.repository;

import com.marchesin.transaction.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    Optional<List<Transaction>> findAllByAccountId(String accountId);
}
