package com.impulse.lean.application.service.interfaces;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.impulse.lean.application.dto.validation.EvidenceValidationRequestDto;
import com.impulse.lean.domain.model.EvidenceValidation;
import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.model.ValidationType;

/**
 * IMPULSE LEAN v1 - Validation Service Interface
 * 
 * Service interface for evidence validation operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public interface ValidationService {

    // Core validation operations
    EvidenceValidation createValidation(String evidenceUuid, String validatorUuid, EvidenceValidationRequestDto request);
    EvidenceValidation startValidation(String validationUuid);
    EvidenceValidation completeValidation(String validationUuid, BigDecimal score, String feedback);
    EvidenceValidation updateValidation(String validationUuid, EvidenceValidationRequestDto request);
    void deleteValidation(String validationUuid);

    // Validation queries
    Optional<EvidenceValidation> findByUuid(String uuid);
    List<EvidenceValidation> findByEvidence(String evidenceUuid);
    List<EvidenceValidation> findByValidator(String validatorUuid);
    Page<EvidenceValidation> findValidations(Pageable pageable);
    Page<EvidenceValidation> searchValidations(String searchTerm, Pageable pageable);

    // Status-based queries
    List<EvidenceValidation> findPendingValidations();
    List<EvidenceValidation> findCompletedValidations();
    List<EvidenceValidation> findValidationsByType(ValidationType type);

    // Validation workflow
    EvidenceValidation assignValidation(String evidenceUuid, String validatorUuid, ValidationType type);
    void reassignValidation(String validationUuid, String newValidatorUuid);
    void escalateValidation(String validationUuid, String reason);

    // Automatic validation
    EvidenceValidation performAutomaticValidation(String evidenceUuid);
    List<EvidenceValidation> processAutomaticValidations();

    // Validation analytics
    BigDecimal getAverageValidationScore();
    BigDecimal getAverageValidationScoreByValidator(String validatorUuid);
    BigDecimal getAverageValidationScoreByEvidence(String evidenceUuid);
    long getValidationCount();
    long getValidationCountByValidator(String validatorUuid);
    long getValidationCountByType(ValidationType type);

    // Validation reports
    List<EvidenceValidation> getValidationsInDateRange(LocalDateTime start, LocalDateTime end);
    List<EvidenceValidation> getHighScoreValidations(BigDecimal minScore);
    List<EvidenceValidation> getLowScoreValidations(BigDecimal maxScore);

    // Validation quality
    double getValidationAccuracy(String validatorUuid);
    List<EvidenceValidation> findInconsistentValidations();
    void flagValidationForReview(String validationUuid, String reason);

    // Validation recommendations
    List<User> getRecommendedValidators(String evidenceUuid);
    ValidationType getRecommendedValidationType(String evidenceUuid);
    BigDecimal predictValidationScore(String evidenceUuid, String validatorUuid);

    // Batch operations
    List<EvidenceValidation> createBulkValidations(List<String> evidenceUuids, String validatorUuid, ValidationType type);
    void processBulkValidations(List<String> validationUuids, BigDecimal score, String feedback);
    void deleteValidationsByEvidence(String evidenceUuid);
    void deleteValidationsByValidator(String validatorUuid);
}
