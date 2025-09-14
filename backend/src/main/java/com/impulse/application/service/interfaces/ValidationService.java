package com.impulse.application.service.interfaces;

import com.impulse.application.dto.evidence.EvidenceValidationRequestDto;
import com.impulse.application.dto.evidence.EvidenceValidationResponseDto;
import com.impulse.application.dto.common.PaginationRequest;
import com.impulse.application.dto.common.PaginationResponse;

import java.util.List;

/**
 * Interface for Validation Service operations
 */
public interface ValidationService {

    /**
     * Submit validation for evidence
     */
    EvidenceValidationResponseDto submitValidation(Long evidenceId, Long validatorId, EvidenceValidationRequestDto request);

    /**
     * Get validation by ID
     */
    EvidenceValidationResponseDto getValidationById(Long validationId);

    /**
     * Get all validations for evidence
     */
    List<EvidenceValidationResponseDto> getValidationsForEvidence(Long evidenceId);

    /**
     * Get all validations by validator
     */
    PaginationResponse<EvidenceValidationResponseDto> getValidationsByValidator(Long validatorId, PaginationRequest request);

    /**
     * Get pending validations for validator
     */
    List<EvidenceValidationResponseDto> getPendingValidationsForValidator(Long validatorId);

    /**
     * Update validation
     */
    EvidenceValidationResponseDto updateValidation(Long validationId, Long validatorId, EvidenceValidationRequestDto request);

    /**
     * Delete validation
     */
    void deleteValidation(Long validationId, Long validatorId);

    /**
     * Check if validator can validate evidence
     */
    boolean canValidatorValidateEvidence(Long validatorId, Long evidenceId);

    /**
     * Check if evidence has been validated by validator
     */
    boolean hasValidatorValidatedEvidence(Long validatorId, Long evidenceId);

    /**
     * Get validation statistics for validator
     */
    ValidationStatsDto getValidationStatistics(Long validatorId);

    /**
     * Get validation consensus for evidence
     */
    ValidationConsensusDto getValidationConsensus(Long evidenceId);

    /**
     * Auto-assign validations to available validators
     */
    void autoAssignValidations();

    /**
     * Escalate validation for manual review
     */
    void escalateValidation(Long validationId, String reason);

    // Inner class for validation statistics
    class ValidationStatsDto {
        private Long totalValidations;
        private Long approvedValidations;
        private Long rejectedValidations;
        private Double averageScore;
        private Double accuracyRate;

        // Constructors
        public ValidationStatsDto() {}

        public ValidationStatsDto(Long totalValidations, Long approvedValidations, Long rejectedValidations, Double averageScore) {
            this.totalValidations = totalValidations;
            this.approvedValidations = approvedValidations;
            this.rejectedValidations = rejectedValidations;
            this.averageScore = averageScore;
            this.accuracyRate = totalValidations > 0 ?
                (approvedValidations.doubleValue() / totalValidations.doubleValue()) * 100 : 0.0;
        }

        // Getters and setters
        public Long getTotalValidations() { return totalValidations; }
        public void setTotalValidations(Long totalValidations) { this.totalValidations = totalValidations; }

        public Long getApprovedValidations() { return approvedValidations; }
        public void setApprovedValidations(Long approvedValidations) { this.approvedValidations = approvedValidations; }

        public Long getRejectedValidations() { return rejectedValidations; }
        public void setRejectedValidations(Long rejectedValidations) { this.rejectedValidations = rejectedValidations; }

        public Double getAverageScore() { return averageScore; }
        public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }

        public Double getAccuracyRate() { return accuracyRate; }
        public void setAccuracyRate(Double accuracyRate) { this.accuracyRate = accuracyRate; }
    }

    // Inner class for validation consensus
    class ValidationConsensusDto {
        private Long evidenceId;
        private Integer totalValidations;
        private Integer approvalCount;
        private Integer rejectionCount;
        private Double consensusScore;
        private String consensusDecision;
        private Double confidenceLevel;

        // Constructors
        public ValidationConsensusDto() {}

        public ValidationConsensusDto(Long evidenceId, Integer totalValidations, Integer approvalCount, Integer rejectionCount) {
            this.evidenceId = evidenceId;
            this.totalValidations = totalValidations;
            this.approvalCount = approvalCount;
            this.rejectionCount = rejectionCount;

            if (totalValidations > 0) {
                this.consensusScore = (approvalCount.doubleValue() / totalValidations.doubleValue()) * 10;
                this.consensusDecision = approvalCount > rejectionCount ? "APPROVED" : "REJECTED";
                this.confidenceLevel = Math.abs(approvalCount - rejectionCount) / (double) totalValidations;
            } else {
                this.consensusScore = 0.0;
                this.consensusDecision = "PENDING";
                this.confidenceLevel = 0.0;
            }
        }

        // Getters and setters
        public Long getEvidenceId() { return evidenceId; }
        public void setEvidenceId(Long evidenceId) { this.evidenceId = evidenceId; }

        public Integer getTotalValidations() { return totalValidations; }
        public void setTotalValidations(Integer totalValidations) { this.totalValidations = totalValidations; }

        public Integer getApprovalCount() { return approvalCount; }
        public void setApprovalCount(Integer approvalCount) { this.approvalCount = approvalCount; }

        public Integer getRejectionCount() { return rejectionCount; }
        public void setRejectionCount(Integer rejectionCount) { this.rejectionCount = rejectionCount; }

        public Double getConsensusScore() { return consensusScore; }
        public void setConsensusScore(Double consensusScore) { this.consensusScore = consensusScore; }

        public String getConsensusDecision() { return consensusDecision; }
        public void setConsensusDecision(String consensusDecision) { this.consensusDecision = consensusDecision; }

        public Double getConfidenceLevel() { return confidenceLevel; }
        public void setConfidenceLevel(Double confidenceLevel) { this.confidenceLevel = confidenceLevel; }
    }
}
