package com.impulse.evidence.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.impulse.evidence.model.Evidence;
import com.impulse.evidence.model.EvidenceValidation;
import com.impulse.user.model.User;
import com.impulse.lean.domain.model.ValidationType;

/**
 * IMPULSE LEAN v1 - Evidence Validation Repository Interface
 * 
 * Repository for evidence validation domain entity operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Repository
public interface EvidenceValidationRepository extends JpaRepository<EvidenceValidation, Long> {

    // Evidence queries
    List<EvidenceValidation> findByEvidence(Evidence evidence);
    List<EvidenceValidation> findByEvidenceOrderByValidatedAtDesc(Evidence evidence);
    
    @Query("SELECT v FROM EvidenceValidation v WHERE v.evidence = :evidence " +
           "AND v.validationType = :type")
    List<EvidenceValidation> findByEvidenceAndType(@Param("evidence") Evidence evidence,
                                                   @Param("type") ValidationType type);

    // Validator queries
    List<EvidenceValidation> findByValidator(User validator);
    List<EvidenceValidation> findByValidatorAndValidationType(User validator, ValidationType type);
    
    @Query("SELECT v FROM EvidenceValidation v WHERE v.validator = :validator " +
           "ORDER BY v.validatedAt DESC")
    Page<EvidenceValidation> findByValidatorOrderByValidatedAtDesc(@Param("validator") User validator, 
                                                                   Pageable pageable);

    // Type queries
    List<EvidenceValidation> findByValidationType(ValidationType type);
    
    @Query("SELECT v FROM EvidenceValidation v WHERE v.validationType = 'AUTOMATIC'")
    List<EvidenceValidation> findAutomaticValidations();

    @Query("SELECT v FROM EvidenceValidation v WHERE v.validationType = 'PEER'")
    List<EvidenceValidation> findPeerValidations();

    @Query("SELECT v FROM EvidenceValidation v WHERE v.validationType = 'MODERATOR'")
    List<EvidenceValidation> findModeratorValidations();

    // Score queries
    @Query("SELECT v FROM EvidenceValidation v WHERE v.score >= :minScore")
    List<EvidenceValidation> findByMinimumScore(@Param("minScore") BigDecimal minScore);

    @Query("SELECT v FROM EvidenceValidation v WHERE v.score < :maxScore")
    List<EvidenceValidation> findByMaximumScore(@Param("maxScore") BigDecimal maxScore);

    @Query("SELECT v FROM EvidenceValidation v WHERE v.score BETWEEN :minScore AND :maxScore")
    List<EvidenceValidation> findByScoreRange(@Param("minScore") BigDecimal minScore,
                                             @Param("maxScore") BigDecimal maxScore);

    // Time-based queries
    @Query("SELECT v FROM EvidenceValidation v WHERE v.validatedAt >= :since")
    List<EvidenceValidation> findValidationsAfter(@Param("since") LocalDateTime since);

    @Query("SELECT v FROM EvidenceValidation v WHERE v.validatedAt BETWEEN :start AND :end")
    List<EvidenceValidation> findValidationsBetween(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);

    // Confidence queries
    @Query("SELECT v FROM EvidenceValidation v WHERE v.confidenceLevel >= :minConfidence")
    List<EvidenceValidation> findHighConfidenceValidations(@Param("minConfidence") BigDecimal minConfidence);

    @Query("SELECT v FROM EvidenceValidation v WHERE v.confidenceLevel < :maxConfidence")
    List<EvidenceValidation> findLowConfidenceValidations(@Param("maxConfidence") BigDecimal maxConfidence);

    // Feedback queries
    @Query("SELECT v FROM EvidenceValidation v WHERE v.feedback IS NOT NULL AND v.feedback != ''")
    List<EvidenceValidation> findValidationsWithFeedback();

    @Query("SELECT v FROM EvidenceValidation v WHERE v.feedback IS NULL OR v.feedback = ''")
    List<EvidenceValidation> findValidationsWithoutFeedback();

    @Query("SELECT v FROM EvidenceValidation v WHERE v.criteriaScores IS NOT NULL AND v.criteriaScores != ''")
    List<EvidenceValidation> findValidationsWithCriteriaScores();

    // Specific validation checks
    Optional<EvidenceValidation> findByEvidenceAndValidator(Evidence evidence, User validator);
    
    boolean existsByEvidenceAndValidator(Evidence evidence, User validator);

    @Query("SELECT COUNT(v) FROM EvidenceValidation v WHERE v.evidence = :evidence " +
           "AND v.validationType = :type")
    long countByEvidenceAndType(@Param("evidence") Evidence evidence, @Param("type") ValidationType type);

    // Statistics queries
    @Query("SELECT COUNT(v) FROM EvidenceValidation v WHERE v.validationType = :type")
    long countByValidationType(@Param("type") ValidationType type);

    @Query("SELECT COUNT(v) FROM EvidenceValidation v WHERE v.validator = :validator")
    long countByValidator(@Param("validator") User validator);

    @Query("SELECT v.validationType, COUNT(v) FROM EvidenceValidation v GROUP BY v.validationType")
    List<Object[]> countValidationsByType();

    @Query("SELECT AVG(v.score) FROM EvidenceValidation v WHERE v.validationType = :type")
    Double getAverageScoreByType(@Param("type") ValidationType type);

    @Query("SELECT AVG(v.score) FROM EvidenceValidation v WHERE v.evidence = :evidence")
    Double getAverageScoreByEvidence(@Param("evidence") Evidence evidence);

    // Additional methods for ValidationServiceImpl
    @Query("SELECT v FROM EvidenceValidation v WHERE v.feedback LIKE %:feedback%")
    Page<EvidenceValidation> findByFeedbackContainingIgnoreCase(@Param("feedback") String feedback, Pageable pageable);

    @Query("SELECT AVG(v.score) FROM EvidenceValidation v")
    Double getAverageScore();

    @Query("SELECT AVG(v.score) FROM EvidenceValidation v WHERE v.validator = :validator")
    Double getAverageScoreByValidator(@Param("validator") User validator);

    @Query("SELECT v FROM EvidenceValidation v WHERE v.validatedAt BETWEEN :start AND :end")
    List<EvidenceValidation> findByValidatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT v FROM EvidenceValidation v WHERE v.score >= :score")
    List<EvidenceValidation> findByScoreGreaterThanEqual(@Param("score") BigDecimal score);

    @Query("SELECT v FROM EvidenceValidation v WHERE v.score <= :score")
    List<EvidenceValidation> findByScoreLessThanEqual(@Param("score") BigDecimal score);

    @Query("SELECT AVG(v.confidenceLevel) FROM EvidenceValidation v WHERE v.confidenceLevel IS NOT NULL")
    Double getAverageConfidenceLevel();

    // Challenge-related queries
    @Query("SELECT v FROM EvidenceValidation v WHERE v.evidence.participation.challenge.id = :challengeId")
    List<EvidenceValidation> findByChallengeId(@Param("challengeId") Long challengeId);

    @Query("SELECT v FROM EvidenceValidation v WHERE v.evidence.participation.challenge.id = :challengeId " +
           "AND v.validationType = :type")
    List<EvidenceValidation> findByChallengeIdAndType(@Param("challengeId") Long challengeId,
                                                     @Param("type") ValidationType type);

    @Query("SELECT AVG(v.score) FROM EvidenceValidation v " +
           "WHERE v.evidence.participation.challenge.id = :challengeId")
    Double getAverageScoreByChallengeId(@Param("challengeId") Long challengeId);

    // User performance queries
    @Query("SELECT v FROM EvidenceValidation v WHERE v.evidence.participation.user = :user")
    List<EvidenceValidation> findValidationsForUser(@Param("user") User user);

    @Query("SELECT AVG(v.score) FROM EvidenceValidation v " +
           "WHERE v.evidence.participation.user = :user")
    Double getAverageScoreForUser(@Param("user") User user);

    @Query("SELECT v FROM EvidenceValidation v WHERE v.validator = :validator " +
           "AND v.validatedAt >= :since " +
           "ORDER BY v.validatedAt DESC")
    List<EvidenceValidation> findRecentValidationsByValidator(@Param("validator") User validator,
                                                             @Param("since") LocalDateTime since);

    // Business logic queries
    @Query("SELECT v FROM EvidenceValidation v WHERE v.evidence = :evidence " +
           "AND v.score >= 0.7")
    List<EvidenceValidation> findPositiveValidations(@Param("evidence") Evidence evidence);

    @Query("SELECT v FROM EvidenceValidation v WHERE v.evidence = :evidence " +
           "AND v.score < 0.3")
    List<EvidenceValidation> findNegativeValidations(@Param("evidence") Evidence evidence);

    @Query("SELECT v FROM EvidenceValidation v WHERE v.validationType != 'AUTOMATIC' " +
           "ORDER BY v.validatedAt DESC")
    List<EvidenceValidation> findHumanValidations(Pageable pageable);

    @Query("SELECT v FROM EvidenceValidation v WHERE v.evidence = :evidence " +
           "ORDER BY v.score DESC, v.confidenceLevel DESC NULLS LAST")
    List<EvidenceValidation> findBestValidationsForEvidence(@Param("evidence") Evidence evidence);

    // Consensus and agreement queries
    @Query("SELECT v.evidence, COUNT(v), AVG(v.score) FROM EvidenceValidation v " +
           "WHERE v.validationType != 'AUTOMATIC' " +
           "GROUP BY v.evidence " +
           "HAVING COUNT(v) >= :minValidations")
    List<Object[]> findEvidenceWithConsensus(@Param("minValidations") int minValidations);

    @Query("SELECT v FROM EvidenceValidation v WHERE v.evidence.id IN " +
           "(SELECT v2.evidence.id FROM EvidenceValidation v2 " +
           "WHERE v2.validationType != 'AUTOMATIC' " +
           "GROUP BY v2.evidence.id " +
           "HAVING COUNT(v2) >= :minValidations)")
    List<EvidenceValidation> findValidationsWithMultipleReviews(@Param("minValidations") int minValidations);
}
