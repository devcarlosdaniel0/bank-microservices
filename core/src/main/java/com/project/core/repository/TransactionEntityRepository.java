package com.project.core.repository;

import com.project.core.domain.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionEntityRepository extends JpaRepository<TransactionEntity, UUID> {
}
