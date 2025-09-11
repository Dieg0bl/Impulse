package com.impulse.lean.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.impulse.lean.domain.model.IdempotencyToken;

@Repository
public interface IdempotencyTokenRepository extends JpaRepository<IdempotencyToken, Long> {
    Optional<IdempotencyToken> findByToken(String token);
}
