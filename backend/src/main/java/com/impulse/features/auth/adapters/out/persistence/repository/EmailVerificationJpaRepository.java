package com.impulse.features.auth.adapters.out.persistence.repository;

import com.impulse.features.auth.adapters.out.persistence.entity.EmailVerificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for EmailVerification
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
@Repository
public interface EmailVerificationJpaRepository extends JpaRepository<EmailVerificationJpaEntity, UUID> {

    Optional<EmailVerificationJpaEntity> findByTokenHash(String tokenHash);

    @Query("SELECT ev FROM EmailVerificationJpaEntity ev " +
           "WHERE ev.userId = :userId " +
           "AND ev.email = :email " +
           "AND ev.verifiedAt IS NULL " +
           "AND ev.expiresAt > :now " +
           "ORDER BY ev.createdAt DESC")
    Optional<EmailVerificationJpaEntity> findActiveByUserIdAndEmail(@Param("userId") Long userId,
                                                                   @Param("email") String email,
                                                                   @Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM EmailVerificationJpaEntity ev " +
           "WHERE ev.expiresAt < :now OR ev.verifiedAt IS NOT NULL")
    int deleteExpiredOrUsedTokens(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(ev) FROM EmailVerificationJpaEntity ev " +
           "WHERE ev.userId = :userId " +
           "AND ev.verifiedAt IS NULL " +
           "AND ev.expiresAt > :now")
    long countActiveByUserId(@Param("userId") Long userId,
                            @Param("now") LocalDateTime now);
}
