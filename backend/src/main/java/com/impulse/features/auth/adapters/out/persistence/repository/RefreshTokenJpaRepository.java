package com.impulse.features.auth.adapters.out.persistence.repository;

import com.impulse.features.auth.adapters.out.persistence.entity.RefreshTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for RefreshToken
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {

    Optional<RefreshTokenJpaEntity> findByTokenHash(String tokenHash);

    @Query("SELECT rt FROM RefreshTokenJpaEntity rt " +
           "WHERE rt.userId = :userId " +
           "AND rt.isRevoked = false " +
           "AND rt.expiresAt > :now")
    List<RefreshTokenJpaEntity> findActiveByUserId(@Param("userId") Long userId,
                                                   @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE RefreshTokenJpaEntity rt " +
           "SET rt.isRevoked = true, rt.revokedAt = :revokedAt " +
           "WHERE rt.userId = :userId")
    int revokeAllByUserId(@Param("userId") Long userId,
                         @Param("revokedAt") LocalDateTime revokedAt);

    @Modifying
    @Query("DELETE FROM RefreshTokenJpaEntity rt " +
           "WHERE rt.expiresAt < :now OR rt.isRevoked = true")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(rt) FROM RefreshTokenJpaEntity rt " +
           "WHERE rt.userId = :userId " +
           "AND rt.isRevoked = false " +
           "AND rt.expiresAt > :now")
    long countActiveByUserId(@Param("userId") Long userId,
                            @Param("now") LocalDateTime now);
}
