package com.impulse.infrastructure.persistence.repository;

import com.impulse.domain.evidence.Evidence;
import com.impulse.domain.evidence.EvidenceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA Repository for Evidence entity
 */
@Repository
public interface EvidenceJpaRepository extends JpaRepository<Evidence, String> {
    
    List<Evidence> findByUserId(String userId);
    
    List<Evidence> findByChallengeId(String challengeId);
    
    List<Evidence> findByStatus(EvidenceStatus status);
    
    List<Evidence> findByValidatorId(String validatorId);
    
    List<Evidence> findByEvidenceType(String evidenceType);
    
    @Query("SELECT e FROM Evidence e WHERE e.userId = :userId AND e.status = :status")
    List<Evidence> findByUserIdAndStatus(@Param("userId") String userId, 
                                        @Param("status") EvidenceStatus status);
    
    @Query("SELECT e FROM Evidence e WHERE e.challengeId = :challengeId AND e.status = :status")
    List<Evidence> findByChallengeIdAndStatus(@Param("challengeId") String challengeId, 
                                             @Param("status") EvidenceStatus status);
    
    @Query("SELECT e FROM Evidence e WHERE e.status = :status AND e.createdAt >= :since")
    List<Evidence> findByStatusAndCreatedAtAfter(@Param("status") EvidenceStatus status, 
                                                @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.challengeId = :challengeId")
    long countByChallengeId(@Param("challengeId") String challengeId);
    
    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.userId = :userId AND e.status = :status")
    long countByUserIdAndStatus(@Param("userId") String userId, 
                               @Param("status") EvidenceStatus status);
    
    @Query("SELECT e FROM Evidence e WHERE e.status = :status ORDER BY e.createdAt ASC")
    List<Evidence> findOldestByStatus(@Param("status") EvidenceStatus status);
    
    @Query("SELECT e FROM Evidence e WHERE " +
           "LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Evidence> searchEvidence(@Param("search") String search);
    
    @Query("SELECT e FROM Evidence e WHERE e.validatorId IS NULL AND e.status = :status")
    List<Evidence> findUnassignedByStatus(@Param("status") EvidenceStatus status);
}
