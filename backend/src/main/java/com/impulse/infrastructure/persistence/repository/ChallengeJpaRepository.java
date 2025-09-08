package com.impulse.infrastructure.persistence.repository;

import com.impulse.domain.challenge.Challenge;
import com.impulse.domain.challenge.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA Repository for Challenge entity
 */
@Repository
public interface ChallengeJpaRepository extends JpaRepository<Challenge, String> {
    
    List<Challenge> findByStatus(ChallengeStatus status);
    
    List<Challenge> findByCreatorId(String creatorId);
    
    List<Challenge> findByChallengeType(String challengeType);
    
    @Query("SELECT c FROM Challenge c WHERE c.status = :status AND " +
           "(c.expiresAt IS NULL OR c.expiresAt > :now)")
    List<Challenge> findActiveAndNotExpired(@Param("status") ChallengeStatus status, 
                                          @Param("now") LocalDateTime now);
    
    @Query("SELECT c FROM Challenge c WHERE c.expiresAt IS NOT NULL AND c.expiresAt < :now")
    List<Challenge> findExpiredChallenges(@Param("now") LocalDateTime now);
    
    @Query("SELECT c FROM Challenge c WHERE c.status = :status AND c.createdAt >= :since")
    List<Challenge> findByStatusAndCreatedAtAfter(@Param("status") ChallengeStatus status, 
                                                 @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(c) FROM Challenge c WHERE c.creatorId = :creatorId AND c.status = :status")
    long countByCreatorIdAndStatus(@Param("creatorId") String creatorId, 
                                  @Param("status") ChallengeStatus status);
    
    @Query("SELECT c FROM Challenge c WHERE " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Challenge> searchChallenges(@Param("search") String search);
    
    @Query("SELECT c FROM Challenge c WHERE c.status = :status " +
           "ORDER BY c.createdAt DESC")
    List<Challenge> findLatestByStatus(@Param("status") ChallengeStatus status);
}
