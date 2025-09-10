package com.impulse.infrastructure.persistence.repositories;

import com.impulse.domain.entities.Evidence;
import com.impulse.domain.entities.Evidence.EvidenceStatus;
import com.impulse.domain.entities.Evidence.ValidationStatus;
import com.impulse.domain.enums.EvidenceType;
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
 * Repository para Evidence
 */
@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    
    // Búsquedas básicas
    List<Evidence> findByChallengeId(Long challengeId);
    Page<Evidence> findByChallengeId(Long challengeId, Pageable pageable);
    
    List<Evidence> findByUserId(Long userId);
    Page<Evidence> findByUserId(Long userId, Pageable pageable);
    
    List<Evidence> findByChallengeIdAndUserId(Long challengeId, Long userId);
    
    // Búsquedas por tipo
    List<Evidence> findByType(EvidenceType type);
    Page<Evidence> findByType(EvidenceType type, Pageable pageable);
    
    List<Evidence> findByChallengeIdAndType(Long challengeId, EvidenceType type);
    
    // Búsquedas por status
    List<Evidence> findByStatus(EvidenceStatus status);
    Page<Evidence> findByStatus(EvidenceStatus status, Pageable pageable);
    
    List<Evidence> findByValidationStatus(ValidationStatus validationStatus);
    Page<Evidence> findByValidationStatus(ValidationStatus validationStatus, Pageable pageable);
    
    // Combinaciones de filtros
    List<Evidence> findByChallengeIdAndStatus(Long challengeId, EvidenceStatus status);
    Page<Evidence> findByChallengeIdAndStatus(Long challengeId, EvidenceStatus status, Pageable pageable);
    
    List<Evidence> findByUserIdAndStatus(Long userId, EvidenceStatus status);
    Page<Evidence> findByUserIdAndStatus(Long userId, EvidenceStatus status, Pageable pageable);
    
    List<Evidence> findByChallengeIdAndValidationStatus(Long challengeId, ValidationStatus validationStatus);
    Page<Evidence> findByChallengeIdAndValidationStatus(Long challengeId, ValidationStatus validationStatus, Pageable pageable);
    
    // Búsquedas por validación
    List<Evidence> findByValidationsCompletedGreaterThanEqual(Integer minValidations);
    List<Evidence> findByValidationsCompletedLessThan(Integer maxValidations);
    
    @Query("SELECT e FROM Evidence e WHERE e.validationsCompleted >= e.validationsRequired")
    List<Evidence> findFullyValidated();
    
    @Query("SELECT e FROM Evidence e WHERE e.validationsCompleted < e.validationsRequired " +
           "AND e.validationStatus = 'PENDING'")
    List<Evidence> findPendingValidation();
    
    @Query("SELECT e FROM Evidence e WHERE e.validationsCompleted >= e.validationsRequired " +
           "AND e.validationStatus = 'PENDING'")
    List<Evidence> findReadyForDecision();
    
    // Búsquedas por scoring
    List<Evidence> findByValidationScoreGreaterThanEqual(BigDecimal minScore);
    List<Evidence> findByValidationScoreLessThan(BigDecimal maxScore);
    
    @Query("SELECT e FROM Evidence e WHERE e.validationScore IS NOT NULL " +
           "ORDER BY e.validationScore DESC")
    Page<Evidence> findTopRatedEvidences(Pageable pageable);
    
    // Búsquedas por flags
    List<Evidence> findByFlaggedForReview(Boolean flagged);
    Page<Evidence> findByFlaggedForReview(Boolean flagged, Pageable pageable);
    
    List<Evidence> findByFlaggedByUserId(Long flaggedByUserId);
    
    @Query("SELECT e FROM Evidence e WHERE e.flaggedForReview = true AND e.flaggedAt >= :since")
    List<Evidence> findRecentlyFlagged(@Param("since") LocalDateTime since);
    
    // Búsquedas por fechas
    List<Evidence> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Evidence> findByValidatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT e FROM Evidence e WHERE e.createdAt >= :date")
    List<Evidence> findRecentEvidences(@Param("date") LocalDateTime date);
    
    // Búsquedas por archivos
    List<Evidence> findByFilePathIsNotNull();
    List<Evidence> findByFilePathIsNull();
    
    List<Evidence> findByMimeType(String mimeType);
    List<Evidence> findByMimeTypeStartingWith(String mimeTypePrefix);
    
    @Query("SELECT e FROM Evidence e WHERE e.fileSize > :size")
    List<Evidence> findLargeFiles(@Param("size") Long minSize);
    
    // Búsqueda de texto
    @Query("SELECT e FROM Evidence e WHERE " +
           "LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.textContent) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Evidence> searchEvidences(@Param("search") String search, Pageable pageable);
    
    // Estadísticas
    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.challengeId = :challengeId")
    long countByChallengeId(@Param("challengeId") Long challengeId);
    
    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.status = :status")
    long countByStatus(@Param("status") EvidenceStatus status);
    
    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.validationStatus = :status")
    long countByValidationStatus(@Param("status") ValidationStatus status);
    
    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.type = :type")
    long countByType(@Param("type") EvidenceType type);
    
    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.createdAt >= :date")
    long countNewEvidencesFromDate(@Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.flaggedForReview = true")
    long countFlaggedEvidences();
    
    // Métricas de validación
    @Query("SELECT AVG(e.validationsCompleted) FROM Evidence e WHERE e.validationStatus = 'APPROVED'")
    Double getAverageValidationsForApproved();
    
    @Query("SELECT AVG(e.validationScore) FROM Evidence e WHERE e.validationScore IS NOT NULL")
    Double getAverageValidationScore();
    
    @Query("SELECT AVG(CAST(e.validationsPositive AS DOUBLE) / e.validationsCompleted * 100) FROM Evidence e " +
           "WHERE e.validationsCompleted > 0")
    Double getAverageApprovalRate();
    
    // Métricas por periodo
    @Query("SELECT DATE(e.createdAt) as date, COUNT(e) as count FROM Evidence e " +
           "WHERE e.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(e.createdAt) ORDER BY date")
    List<Object[]> getEvidenceSubmissionsByDay(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e.type, COUNT(e) as count FROM Evidence e " +
           "WHERE e.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY e.type ORDER BY count DESC")
    List<Object[]> getEvidencesByType(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
    
    // Top usuarios por evidencias
    @Query("SELECT e.userId, COUNT(e) as evidenceCount FROM Evidence e " +
           "WHERE e.status = 'ACCEPTED' GROUP BY e.userId ORDER BY evidenceCount DESC")
    List<Object[]> getTopEvidenceContributors(Pageable pageable);
    
    @Query("SELECT e.userId, AVG(e.validationScore) as avgScore FROM Evidence e " +
           "WHERE e.validationScore IS NOT NULL GROUP BY e.userId ORDER BY avgScore DESC")
    List<Object[]> getTopQualityContributors(Pageable pageable);
    
    // Evidencias para revisión
    @Query("SELECT e FROM Evidence e WHERE e.validationStatus = 'UNDER_REVIEW' " +
           "ORDER BY e.createdAt ASC")
    List<Evidence> findEvidencesForReview();
    
    @Query("SELECT e FROM Evidence e WHERE e.validationsCompleted < e.validationsRequired " +
           "AND e.validationStatus = 'PENDING' AND e.status = 'PENDING' " +
           "ORDER BY e.createdAt ASC")
    List<Evidence> findEvidencesNeedingValidation();
    
    // Evidencias duplicadas potenciales
    @Query("SELECT e FROM Evidence e WHERE e.challengeId = :challengeId AND e.userId = :userId " +
           "AND e.type = :type AND e.title = :title AND e.id != :excludeId")
    List<Evidence> findPotentialDuplicates(@Param("challengeId") Long challengeId,
                                         @Param("userId") Long userId,
                                         @Param("type") EvidenceType type,
                                         @Param("title") String title,
                                         @Param("excludeId") Long excludeId);
    
    // Evidencias por challenge y usuario (para límites)
    @Query("SELECT COUNT(e) FROM Evidence e WHERE e.challengeId = :challengeId AND e.userId = :userId")
    long countByChallengeIdAndUserId(@Param("challengeId") Long challengeId, @Param("userId") Long userId);
    
    // Evidencias con mayor engagement
    @Query("SELECT e FROM Evidence e WHERE e.validationsCompleted > 0 " +
           "ORDER BY e.validationsCompleted DESC, e.validationScore DESC")
    Page<Evidence> findMostEngagedEvidences(Pageable pageable);
    
    // Metadata queries
    @Query("SELECT e FROM Evidence e JOIN e.metadata m WHERE KEY(m) = :key AND VALUE(m) = :value")
    List<Evidence> findByMetadata(@Param("key") String key, @Param("value") String value);
    
    // Evidencias por rango de puntos
    List<Evidence> findByPointsAwardedGreaterThan(Integer minPoints);
    List<Evidence> findByPointsAwardedBetween(Integer minPoints, Integer maxPoints);
}
