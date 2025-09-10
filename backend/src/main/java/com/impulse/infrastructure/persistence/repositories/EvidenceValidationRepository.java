package com.impulse.infrastructure.persistence.repositories;

import com.impulse.domain.entities.EvidenceValidation;
import com.impulse.domain.entities.EvidenceValidation.ValidationStatus;
import com.impulse.domain.entities.EvidenceValidation.ValidationDecision;
import com.impulse.domain.entities.EvidenceValidation.ValidationPriority;
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
 * Repository para EvidenceValidation
 */
@Repository
public interface EvidenceValidationRepository extends JpaRepository<EvidenceValidation, Long> {
    
    // Búsquedas básicas
    List<EvidenceValidation> findByEvidenceId(Long evidenceId);
    Page<EvidenceValidation> findByEvidenceId(Long evidenceId, Pageable pageable);
    
    List<EvidenceValidation> findByValidatorId(Long validatorId);
    Page<EvidenceValidation> findByValidatorId(Long validatorId, Pageable pageable);
    
    Optional<EvidenceValidation> findByEvidenceIdAndValidatorId(Long evidenceId, Long validatorId);
    
    // Verificaciones de existencia
    boolean existsByEvidenceIdAndValidatorId(Long evidenceId, Long validatorId);
    
    // Búsquedas por status
    List<EvidenceValidation> findByStatus(ValidationStatus status);
    Page<EvidenceValidation> findByStatus(ValidationStatus status, Pageable pageable);
    
    List<EvidenceValidation> findByValidatorIdAndStatus(Long validatorId, ValidationStatus status);
    Page<EvidenceValidation> findByValidatorIdAndStatus(Long validatorId, ValidationStatus status, Pageable pageable);
    
    List<EvidenceValidation> findByEvidenceIdAndStatus(Long evidenceId, ValidationStatus status);
    
    // Búsquedas por decisión
    List<EvidenceValidation> findByDecision(ValidationDecision decision);
    Page<EvidenceValidation> findByDecision(ValidationDecision decision, Pageable pageable);
    
    List<EvidenceValidation> findByValidatorIdAndDecision(Long validatorId, ValidationDecision decision);
    List<EvidenceValidation> findByEvidenceIdAndDecision(Long evidenceId, ValidationDecision decision);
    
    // Búsquedas por prioridad
    List<EvidenceValidation> findByPriority(ValidationPriority priority);
    Page<EvidenceValidation> findByPriority(ValidationPriority priority, Pageable pageable);
    
    @Query("SELECT v FROM EvidenceValidation v WHERE v.priority IN ('HIGH', 'URGENT') " +
           "AND v.status IN ('PENDING', 'IN_PROGRESS') ORDER BY v.priority DESC, v.assignedAt ASC")
    List<EvidenceValidation> findHighPriorityPending();
    
    // Validaciones pendientes
    @Query("SELECT v FROM EvidenceValidation v WHERE v.status = 'PENDING' " +
           "ORDER BY v.priority DESC, v.assignedAt ASC")
    List<EvidenceValidation> findPendingValidations();
    
    @Query("SELECT v FROM EvidenceValidation v WHERE v.validatorId = :validatorId " +
           "AND v.status = 'PENDING' ORDER BY v.priority DESC, v.assignedAt ASC")
    List<EvidenceValidation> findPendingForValidator(@Param("validatorId") Long validatorId);
    
    @Query("SELECT v FROM EvidenceValidation v WHERE v.status = 'IN_PROGRESS' " +
           "ORDER BY v.startedAt ASC")
    List<EvidenceValidation> findInProgressValidations();
    
    // Validaciones completadas
    @Query("SELECT v FROM EvidenceValidation v WHERE v.status = 'COMPLETED' " +
           "ORDER BY v.completedAt DESC")
    Page<EvidenceValidation> findCompletedValidations(Pageable pageable);
    
    @Query("SELECT v FROM EvidenceValidation v WHERE v.validatorId = :validatorId " +
           "AND v.status = 'COMPLETED' ORDER BY v.completedAt DESC")
    Page<EvidenceValidation> findCompletedByValidator(@Param("validatorId") Long validatorId, Pageable pageable);
    
    // Validaciones expiradas
    @Query("SELECT v FROM EvidenceValidation v WHERE v.status = 'EXPIRED' " +
           "ORDER BY v.assignedAt DESC")
    List<EvidenceValidation> findExpiredValidations();
    
    @Query("SELECT v FROM EvidenceValidation v WHERE v.status IN ('PENDING', 'IN_PROGRESS') " +
           "AND v.assignedAt < :expiryDate")
    List<EvidenceValidation> findValidationsToExpire(@Param("expiryDate") LocalDateTime expiryDate);
    
    // Búsquedas por scoring
    List<EvidenceValidation> findByOverallScoreGreaterThanEqual(BigDecimal minScore);
    List<EvidenceValidation> findByOverallScoreLessThan(BigDecimal maxScore);
    
    @Query("SELECT v FROM EvidenceValidation v WHERE v.overallScore IS NOT NULL " +
           "ORDER BY v.overallScore DESC")
    Page<EvidenceValidation> findTopScoredValidations(Pageable pageable);
    
    // Búsquedas por fechas
    List<EvidenceValidation> findByAssignedAtBetween(LocalDateTime start, LocalDateTime end);
    List<EvidenceValidation> findByCompletedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT v FROM EvidenceValidation v WHERE v.assignedAt >= :date")
    List<EvidenceValidation> findRecentlyAssigned(@Param("date") LocalDateTime date);
    
    // Búsquedas por tiempo dedicado
    List<EvidenceValidation> findByTimeSpentMinutesGreaterThan(Integer minTime);
    List<EvidenceValidation> findByTimeSpentMinutesBetween(Integer minTime, Integer maxTime);
    
    // Búsquedas por consistencia
    List<EvidenceValidation> findByIsConsistentWithPrevious(Boolean consistent);
    
    @Query("SELECT v FROM EvidenceValidation v WHERE v.isConsistentWithPrevious = false " +
           "AND v.status = 'COMPLETED'")
    List<EvidenceValidation> findInconsistentValidations();
    
    // Búsquedas por confianza
    List<EvidenceValidation> findByConfidenceLevelGreaterThanEqual(BigDecimal minConfidence);
    List<EvidenceValidation> findByConfidenceLevelLessThan(BigDecimal maxConfidence);
    
    // Estadísticas
    @Query("SELECT COUNT(v) FROM EvidenceValidation v WHERE v.evidenceId = :evidenceId")
    long countByEvidenceId(@Param("evidenceId") Long evidenceId);
    
    @Query("SELECT COUNT(v) FROM EvidenceValidation v WHERE v.validatorId = :validatorId")
    long countByValidatorId(@Param("validatorId") Long validatorId);
    
    @Query("SELECT COUNT(v) FROM EvidenceValidation v WHERE v.status = :status")
    long countByStatus(@Param("status") ValidationStatus status);
    
    @Query("SELECT COUNT(v) FROM EvidenceValidation v WHERE v.decision = :decision")
    long countByDecision(@Param("decision") ValidationDecision decision);
    
    @Query("SELECT COUNT(v) FROM EvidenceValidation v WHERE v.validatorId = :validatorId " +
           "AND v.status = 'COMPLETED'")
    long countCompletedByValidator(@Param("validatorId") Long validatorId);
    
    @Query("SELECT COUNT(v) FROM EvidenceValidation v WHERE v.validatorId = :validatorId " +
           "AND v.decision = :decision")
    long countByValidatorAndDecision(@Param("validatorId") Long validatorId, 
                                   @Param("decision") ValidationDecision decision);
    
    // Métricas de rendimiento
    @Query("SELECT AVG(v.timeSpentMinutes) FROM EvidenceValidation v WHERE v.status = 'COMPLETED' " +
           "AND v.timeSpentMinutes > 0")
    Double getAverageValidationTime();
    
    @Query("SELECT AVG(v.timeSpentMinutes) FROM EvidenceValidation v WHERE v.validatorId = :validatorId " +
           "AND v.status = 'COMPLETED' AND v.timeSpentMinutes > 0")
    Double getAverageValidationTimeForValidator(@Param("validatorId") Long validatorId);
    
    @Query("SELECT AVG(v.overallScore) FROM EvidenceValidation v WHERE v.overallScore IS NOT NULL")
    Double getAverageOverallScore();
    
    @Query("SELECT AVG(v.overallScore) FROM EvidenceValidation v WHERE v.validatorId = :validatorId " +
           "AND v.overallScore IS NOT NULL")
    Double getAverageScoreByValidator(@Param("validatorId") Long validatorId);
    
    // Métricas por periodo
    @Query("SELECT DATE(v.completedAt) as date, COUNT(v) as count FROM EvidenceValidation v " +
           "WHERE v.completedAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(v.completedAt) ORDER BY date")
    List<Object[]> getValidationCompletionsByDay(@Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT v.decision, COUNT(v) as count FROM EvidenceValidation v " +
           "WHERE v.completedAt BETWEEN :startDate AND :endDate " +
           "GROUP BY v.decision ORDER BY count DESC")
    List<Object[]> getValidationDecisionsByPeriod(@Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
    
    // Top validadores
    @Query("SELECT v.validatorId, COUNT(v) as validationCount FROM EvidenceValidation v " +
           "WHERE v.status = 'COMPLETED' GROUP BY v.validatorId ORDER BY validationCount DESC")
    List<Object[]> getTopValidators(Pageable pageable);
    
    @Query("SELECT v.validatorId, AVG(v.overallScore) as avgScore FROM EvidenceValidation v " +
           "WHERE v.overallScore IS NOT NULL GROUP BY v.validatorId ORDER BY avgScore DESC")
    List<Object[]> getTopScoringValidators(Pageable pageable);
    
    @Query("SELECT v.validatorId, AVG(v.timeSpentMinutes) as avgTime FROM EvidenceValidation v " +
           "WHERE v.timeSpentMinutes > 0 AND v.status = 'COMPLETED' " +
           "GROUP BY v.validatorId ORDER BY avgTime ASC")
    List<Object[]> getFastestValidators(Pageable pageable);
    
    // Validaciones por evidencia específica con decisiones
    @Query("SELECT v.decision, COUNT(v) as count FROM EvidenceValidation v " +
           "WHERE v.evidenceId = :evidenceId AND v.status = 'COMPLETED' " +
           "GROUP BY v.decision")
    List<Object[]> getValidationDecisionsForEvidence(@Param("evidenceId") Long evidenceId);
    
    // Carga de trabajo de validadores
    @Query("SELECT v.validatorId, COUNT(v) as pendingCount FROM EvidenceValidation v " +
           "WHERE v.status IN ('PENDING', 'IN_PROGRESS') GROUP BY v.validatorId")
    List<Object[]> getValidatorWorkload();
    
    @Query("SELECT v FROM EvidenceValidation v WHERE v.validatorId = :validatorId " +
           "AND v.status IN ('PENDING', 'IN_PROGRESS') ORDER BY v.priority DESC, v.assignedAt ASC")
    List<EvidenceValidation> getValidatorCurrentWork(@Param("validatorId") Long validatorId);
    
    // Análisis de calidad de validadores
    @Query("SELECT v.validatorId, " +
           "AVG(CASE WHEN v.isConsistentWithPrevious = true THEN 1.0 ELSE 0.0 END) as consistencyRate " +
           "FROM EvidenceValidation v WHERE v.status = 'COMPLETED' " +
           "GROUP BY v.validatorId ORDER BY consistencyRate DESC")
    List<Object[]> getValidatorConsistencyRates();
    
    @Query("SELECT v.validatorId, AVG(v.confidenceLevel) as avgConfidence " +
           "FROM EvidenceValidation v WHERE v.confidenceLevel IS NOT NULL " +
           "GROUP BY v.validatorId ORDER BY avgConfidence DESC")
    List<Object[]> getValidatorConfidenceLevels();
    
    // Metadata queries
    @Query("SELECT v FROM EvidenceValidation v JOIN v.metadata m WHERE KEY(m) = :key AND VALUE(m) = :value")
    List<EvidenceValidation> findByMetadata(@Param("key") String key, @Param("value") String value);
}
