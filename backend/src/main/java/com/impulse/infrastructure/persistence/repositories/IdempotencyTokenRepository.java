package com.impulse.infrastructure.persistence.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.impulse.domain.model.IdempotencyToken;

/**
 * Repository interface for IdempotencyToken entity operations
 */
@Repository
public interface IdempotencyTokenRepository extends JpaRepository<IdempotencyToken, Long> {

    /**
     * Find token by token value
     */
    Optional<IdempotencyToken> findByToken(String token);

    /**
     * Find token by token value and user ID
     */
    Optional<IdempotencyToken> findByTokenAndUserId(String token, Long userId);

    /**
     * Find active tokens for user
     */
    @Query("SELECT it FROM IdempotencyToken it WHERE it.userId = :userId " +
           "AND it.isUsed = false AND it.expiresAt > :currentTime")
    List<IdempotencyToken> findActiveTokensByUser(@Param("userId") Long userId,
                                                  @Param("currentTime") LocalDateTime currentTime);

    /**
     * Find expired tokens
     */
    @Query("SELECT it FROM IdempotencyToken it WHERE it.expiresAt < :currentTime")
    List<IdempotencyToken> findExpiredTokens(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find used tokens
     */
    List<IdempotencyToken> findByIsUsed(Boolean isUsed);

    /**
     * Find tokens by operation type
     */
    List<IdempotencyToken> findByOperationType(String operationType);

    /**
     * Find tokens by user ID
     */
    List<IdempotencyToken> findByUserId(Long userId);

    /**
     * Check if token exists and is valid
     */
    @Query("SELECT COUNT(it) > 0 FROM IdempotencyToken it WHERE it.token = :token " +
           "AND it.isUsed = false AND it.expiresAt > :currentTime")
    Boolean isTokenValid(@Param("token") String token, @Param("currentTime") LocalDateTime currentTime);

    /**
     * Check if token exists for operation
     */
    @Query("SELECT COUNT(it) > 0 FROM IdempotencyToken it WHERE it.token = :token " +
           "AND it.operationType = :operationType AND it.isUsed = false")
    Boolean existsByTokenAndOperationType(@Param("token") String token,
                                         @Param("operationType") String operationType);

    /**
     * Find tokens created in date range
     */
    @Query("SELECT it FROM IdempotencyToken it WHERE it.createdAt BETWEEN :startDate AND :endDate")
    List<IdempotencyToken> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    /**
     * Find tokens with pagination
     */
    Page<IdempotencyToken> findByUserId(Long userId, Pageable pageable);

    /**
     * Mark token as used
     */
    @Query("UPDATE IdempotencyToken it SET it.isUsed = true, it.usedAt = :usedTime " +
           "WHERE it.token = :token")
    void markTokenAsUsed(@Param("token") String token, @Param("usedTime") LocalDateTime usedTime);

    /**
     * Delete expired tokens
     */
    @Query("DELETE FROM IdempotencyToken it WHERE it.expiresAt < :currentTime")
    void deleteExpiredTokens(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Count active tokens by user
     */
    @Query("SELECT COUNT(it) FROM IdempotencyToken it WHERE it.userId = :userId " +
           "AND it.isUsed = false AND it.expiresAt > :currentTime")
    Long countActiveTokensByUser(@Param("userId") Long userId,
                                @Param("currentTime") LocalDateTime currentTime);

    /**
     * Find tokens by resource ID
     */
    @Query("SELECT it FROM IdempotencyToken it WHERE it.resourceId = :resourceId")
    List<IdempotencyToken> findByResourceId(@Param("resourceId") String resourceId);

    /**
     * Find latest token for user and operation
     */
    @Query("SELECT it FROM IdempotencyToken it WHERE it.userId = :userId " +
           "AND it.operationType = :operationType ORDER BY it.createdAt DESC")
    List<IdempotencyToken> findLatestByUserAndOperation(@Param("userId") Long userId,
                                                        @Param("operationType") String operationType,
                                                        Pageable pageable);
}
