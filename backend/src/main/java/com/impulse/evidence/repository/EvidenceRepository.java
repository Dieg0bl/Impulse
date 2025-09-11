package com.impulse.evidence.repository;

import com.impulse.evidence.model.Evidence;
import com.impulse.evidence.model.EvidenceStatus;
import com.impulse.evidence.model.EvidenceType;
import com.impulse.challenge.model.ChallengeParticipation;
import com.impulse.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * IMPULSE LEAN v1 - Evidence Repository Interface
 * 
 * Repository for evidence domain entity operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {

    // Basic lookups
    Optional<Evidence> findByUuid(String uuid);
    
    // Participation queries
    List<Evidence> findByParticipation(ChallengeParticipation participation);
    List<Evidence> findByParticipationAndStatus(ChallengeParticipation participation, EvidenceStatus status);
    
    @Query("SELECT e FROM Evidence e WHERE e.participation = :participation " +
           "ORDER BY e.submittedAt DESC")
    Page<Evidence> findByParticipationOrderBySubmittedAtDesc(@Param("participation") ChallengeParticipation participation,
                                                             Pageable pageable);

    // Status queries
    List<Evidence> findByStatus(EvidenceStatus status);
    
    @Query("SELECT e FROM Evidence e WHERE e.status = 'PENDING'")
    List<Evidence> findPendingEvidence();

    @Query("SELECT e FROM Evidence e WHERE e.status = 'APPROVED'")
    List<Evidence> findApprovedEvidence();

    @Query("SELECT e FROM Evidence e WHERE e.status = 'REJECTED'")
    List<Evidence> findRejectedEvidence();

    // Type queries
    List<Evidence> findByType(EvidenceType type);
    
    @Query("SELECT e FROM Evidence e WHERE e.type = :type AND e.status = 'APPROVED'")
    List<Evidence> findApprovedEvidenceByType(@Param("type") EvidenceType type);

    // User queries (through participation)
    @Query("SELECT e FROM Evidence e WHERE e.participation.user = :user")
    List<Evidence> findByUser(@Param("user") User user);

    @Query("SELECT e FROM Evidence e WHERE e.participation.user = :user " +
           "AND e.status = :status")
    List<Evidence> findByUserAndStatus(@Param("user") User user, @Param("status") EvidenceStatus status);

    @Query("SELECT e FROM Evidence e WHERE e.participation.user = :user " +
           "ORDER BY e.submittedAt DESC")
    Page<Evidence> findByUserOrderBySubmittedAtDesc(@Param("user") User user, Pageable pageable);

    // Time-based queries
    @Query("SELECT e FROM Evidence e WHERE e.submittedAt >= :since")
    List<Evidence> findEvidenceSubmittedAfter(@Param("since") LocalDateTime since);

    @Query("SELECT e FROM Evidence e WHERE e.validatedAt >= :since")
    List<Evidence> findEvidenceValidatedAfter(@Param("since") LocalDateTime since);

    @Query("SELECT e FROM Evidence e WHERE e.status = 'PENDING' " +
           "AND e.submittedAt < :threshold")
    List<Evidence> findOldPendingEvidence(@Param("threshold") LocalDateTime threshold);

    // Validation score queries
    @Query("SELECT e FROM Evidence e WHERE e.validationScore >= :minScore " +
           "AND e.status = 'APPROVED'")
    List<Evidence> findHighQualityEvidence(@Param("minScore") BigDecimal minScore);

    @Query("SELECT e FROM Evidence e WHERE e.validationScore < :maxScore " +
           "AND e.status = 'APPROVED'")
    List<Evidence> findLowQualityEvidence(@Param("maxScore") BigDecimal maxScore);

    @Query("SELECT e FROM Evidence e WHERE e.validationScore IS NOT NULL " +
           "ORDER BY e.validationScore DESC")
    List<Evidence> findTopScoredEvidence(Pageable pageable);

    // File size and content queries
    @Query("SELECT e FROM Evidence e WHERE e.fileSize > :maxSize")
    List<Evidence> findLargeFiles(@Param("maxSize") Long maxSize);

    @Query("SELECT e FROM Evidence e WHERE e.mimeType = :mimeType")
    List<Evidence> findByMimeType(@Param("mimeType") String mimeType);

    @Query("SELECT e FROM Evidence e WHERE e.mimeType LIKE :mimeTypePattern")
    List<Evidence> findByMimeTypePattern(@Param("mimeTypePattern") String mimeTypePattern);

    // Challenge-related queries
    @Query("SELECT e FROM Evidence e WHERE e.participation.challenge.id = :challengeId")
    List<Evidence> findByChallengeId(@Param("challengeId") Long challengeId);

    @Query("SELECT e FROM Evidence e WHERE e.participation.challenge.id = :challengeId " +
           "AND e.status = :status")
    List<Evidence> findByChallengeIdAndStatus(@Param("challengeId") Long challengeId, 
                                             @Param("status") EvidenceStatus status);

    @Query("SELECT e FROM Evidence e WHERE e.participation.challenge.id = :challengeId " +
           "ORDER BY e.validationScore DESC NULLS LAST")
    List<Evidence> findByChallengeIdOrderByScore(@Param("challengeId") Long challengeId, Pageable pageable);

    // Statistics queries
    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.status = :status")
    long countByStatus(@Param("status") EvidenceStatus status);

    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.type = :type")
    long countByType(@Param("type") EvidenceType type);

    @Query("SELECT e.status, COUNT(e) FROM Evidence e GROUP BY e.status")
    List<Object[]> countEvidenceByStatus();

    @Query("SELECT e.type, COUNT(e) FROM Evidence e GROUP BY e.type")
    List<Object[]> countEvidenceByType();

    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.participation.user = :user")
    long countByUser(@Param("user") User user);

    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.participation.challenge.id = :challengeId")
    long countByChallengeId(@Param("challengeId") Long challengeId);

    // Search capabilities
    @Query("SELECT e FROM Evidence e WHERE " +
           "LOWER(e.content) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Evidence> searchEvidence(@Param("search") String searchTerm, Pageable pageable);

    @Query("SELECT e FROM Evidence e WHERE " +
           "(LOWER(e.content) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND e.status = 'APPROVED'")
    Page<Evidence> searchApprovedEvidence(@Param("search") String searchTerm, Pageable pageable);

    // Validation queries
    @Query("SELECT e FROM Evidence e WHERE SIZE(e.validations) = 0 " +
           "AND e.status = 'PENDING'")
    List<Evidence> findUnvalidatedEvidence();

    @Query("SELECT e FROM Evidence e WHERE SIZE(e.validations) > 0 " +
           "AND e.status = 'PENDING'")
    List<Evidence> findPartiallyValidatedEvidence();

    @Query("SELECT e FROM Evidence e WHERE SIZE(e.validations) >= :minValidations")
    List<Evidence> findEvidenceWithMinValidations(@Param("minValidations") int minValidations);

    // Business logic queries
    @Query("SELECT e FROM Evidence e WHERE e.participation.user = :user " +
           "AND e.participation.challenge.id = :challengeId")
    List<Evidence> findByUserAndChallengeId(@Param("user") User user, @Param("challengeId") Long challengeId);

    @Query("SELECT e FROM Evidence e WHERE e.status = 'PENDING' " +
           "AND e.participation.challenge.status = 'ACTIVE' " +
           "ORDER BY e.submittedAt ASC")
    List<Evidence> findPendingEvidenceForActiveChallenges();

    @Query("SELECT e FROM Evidence e WHERE e.status = 'APPROVED' " +
           "AND e.participation.challenge.id = :challengeId " +
           "AND e.validationScore >= :minScore " +
           "ORDER BY e.validationScore DESC")
    List<Evidence> findTopEvidenceForChallenge(@Param("challengeId") Long challengeId,
                                              @Param("minScore") BigDecimal minScore,
                                              Pageable pageable);

    // Analytics queries
    @Query("SELECT AVG(e.validationScore) FROM Evidence e WHERE e.status = 'APPROVED' " +
           "AND e.validationScore IS NOT NULL")
    Double getAverageValidationScore();

    @Query("SELECT AVG(e.validationScore) FROM Evidence e WHERE e.status = 'APPROVED' " +
           "AND e.validationScore IS NOT NULL " +
           "AND e.participation.challenge.id = :challengeId")
    Double getAverageValidationScoreByChallenge(@Param("challengeId") Long challengeId);

    @Query("SELECT AVG(e.fileSize) FROM Evidence e WHERE e.fileSize IS NOT NULL")
    Double getAverageFileSize();
}
