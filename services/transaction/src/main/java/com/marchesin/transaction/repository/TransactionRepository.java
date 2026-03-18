package com.marchesin.transaction.repository;

import com.marchesin.transaction.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findAllBySourceAccountIdOrTargetAccountIdOrderByTimeStampDesc(String sourceId, String targetId);
}
