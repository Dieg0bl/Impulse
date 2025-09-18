package com.impulse.infrastructure.persistence.repositories;

import com.impulse.shared.annotations.Generated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Idempotency Token Repository
 * Persistence layer for idempotency_keys table
 */
@Generated
@Repository
public interface IdempotencyTokenRepository extends JpaRepository<Object, Long> {
    // TODO: Define entity and operations
    // - findByKeyHash(byte[] keyHash)
    // - Save idempotency token with response
}
