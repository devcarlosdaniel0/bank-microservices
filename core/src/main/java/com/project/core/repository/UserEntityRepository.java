package com.project.core.repository;


import com.project.core.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByConfirmationToken(String token);

    List<UserEntity> findAllByIsConfirmedFalseAndConfirmationTokenExpirationBefore(LocalDateTime expirationTime);
}
