package com.impulse.lean.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.impulse.lean.domain.model.EvidenceValidation;
import com.impulse.lean.domain.model.ValidationType;

/**
 * IMPULSE LEAN v1 - Evidence Validation Service Interface
 * 
 * Service interface for managing evidence validations
 * Compatible with existing EvidenceValidation model
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public interface EvidenceValidationService {

    /**
     * Create a new evidence validation
     */
    EvidenceValidation createValidation(Long evidenceId, Long validatorId, 
                                      ValidationType type, BigDecimal score, String feedback);

    /**
     * Update an existing validation
     */
    EvidenceValidation updateValidation(Long validationId, BigDecimal score, 
                                      String feedback, BigDecimal confidenceLevel);

    /**
     * Get validation by ID
     */
    Optional<EvidenceValidation> getValidationById(Long validationId);

    /**
     * Get all validations for an evidence
     */
    List<EvidenceValidation> getValidationsByEvidence(Long evidenceId);

    /**
     * Get all validations by a validator
     */
    List<EvidenceValidation> getValidationsByValidator(Long validatorId);

    /**
     * Get validations with pagination
     */
    Page<EvidenceValidation> getValidations(Pageable pageable);

    /**
     * Delete a validation
     */
    boolean deleteValidation(Long validationId);

    /**
     * Assign validation to a validator
     */
    EvidenceValidation assignValidation(Long evidenceId, Long validatorId);

    /**
     * Perform automatic validation
     */
    EvidenceValidation performAutomaticValidation(Long evidenceId);

    /**
     * Get validations by type
     */
    List<EvidenceValidation> getValidationsByType(ValidationType type);

    /**
     * Get validation statistics for evidence
     */
    Map<String, Object> getValidationStats(Long evidenceId);

    /**
     * Get validator performance metrics
     */
    Map<String, Object> getValidatorMetrics(Long validatorId);

    /**
     * Get validations in date range
     */
    List<EvidenceValidation> getValidationsByDateRange(LocalDateTime start, LocalDateTime end);

    /**
     * Check if evidence has conflicting validations
     */
    boolean hasConflictingValidations(Long evidenceId);

    /**
     * Get average score for evidence
     */
    BigDecimal getAverageScore(Long evidenceId);

    /**
     * Get validation analytics
     */
    Map<String, Object> getValidationAnalytics(LocalDateTime start, LocalDateTime end);
}
