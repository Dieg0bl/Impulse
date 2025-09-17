package com.impulse.application.service.interfaces;

import com.impulse.application.dto.validator.ValidatorRequestDto;
import com.impulse.application.dto.validator.ValidatorAssignmentRequestDto;
import com.impulse.domain.validator.Validator;
import com.impulse.domain.validatorassignment.ValidatorAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing validators and their assignments
 */
public interface ValidatorService {

    /**
     * Create a new validator
     */
    Validator createValidator(ValidatorRequestDto requestDto);

    /**
     * Update an existing validator
     */
    Validator updateValidator(Long validatorId, ValidatorRequestDto requestDto);

    /**
     * Get validator by ID
     */
    Optional<Validator> getValidatorById(Long validatorId);

    /**
     * Get validator by user ID
     */
    Optional<Validator> getValidatorByUserId(Long userId);

    /**
     * Get all validators with pagination
     */
    Page<Validator> getAllValidators(Pageable pageable);

    /**
     * Get active validators with pagination
     */
    Page<Validator> getActiveValidators(Pageable pageable);

    /**
     * Get validators by specialization
     */
    List<Validator> getValidatorsBySpecialization(String specialization);

    /**
     * Get validators by minimum rating
     */
    List<Validator> getValidatorsByMinimumRating(Double minimumRating);

    /**
     * Assign validator to evidence
     */
    ValidatorAssignment assignValidatorToEvidence(ValidatorAssignmentRequestDto requestDto);

    /**
     * Get validator assignments for a validator
     */
    List<ValidatorAssignment> getValidatorAssignments(Long validatorId);

    /**
     * Get pending assignments for a validator
     */
    List<ValidatorAssignment> getPendingAssignments(Long validatorId);

    /**
     * Complete validator assignment
     */
    ValidatorAssignment completeAssignment(Long assignmentId, String validationResult, String notes);

    /**
     * Update validator rating
     */
    Validator updateValidatorRating(Long validatorId, Double newRating);

    /**
     * Activate validator
     */
    Validator activateValidator(Long validatorId);

    /**
     * Deactivate validator
     */
    Validator deactivateValidator(Long validatorId);

    /**
     * Delete validator
     */
    void deleteValidator(Long validatorId);

    /**
     * Get validator statistics
     */
    ValidatorStats getValidatorStatistics(Long validatorId);

    /**
     * Get available validators for assignment
     */
    List<Validator> getAvailableValidatorsForAssignment(String challengeType, String specialization);

    /**
     * Check if user can be a validator
     */
    boolean canUserBeValidator(Long userId);

    /**
     * Get validator workload
     */
    int getValidatorWorkload(Long validatorId);

    /**
     * Update validator availability
     */
    Validator updateValidatorAvailability(Long validatorId, boolean available);

    // Inner class for validator statistics
    class ValidatorStats {
        private Long totalAssignments;
        private Long completedAssignments;
        private Long pendingAssignments;
        private Double averageRating;
        private Double completionRate;

        // Constructors
        public ValidatorStats() {}

        public ValidatorStats(Long totalAssignments, Long completedAssignments,
                            Long pendingAssignments, Double averageRating, Double completionRate) {
            this.totalAssignments = totalAssignments;
            this.completedAssignments = completedAssignments;
            this.pendingAssignments = pendingAssignments;
            this.averageRating = averageRating;
            this.completionRate = completionRate;
        }

        // Getters and Setters
        public Long getTotalAssignments() { return totalAssignments; }
        public void setTotalAssignments(Long totalAssignments) { this.totalAssignments = totalAssignments; }

        public Long getCompletedAssignments() { return completedAssignments; }
        public void setCompletedAssignments(Long completedAssignments) { this.completedAssignments = completedAssignments; }

        public Long getPendingAssignments() { return pendingAssignments; }
        public void setPendingAssignments(Long pendingAssignments) { this.pendingAssignments = pendingAssignments; }

        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

        public Double getCompletionRate() { return completionRate; }
        public void setCompletionRate(Double completionRate) { this.completionRate = completionRate; }
    }
}

