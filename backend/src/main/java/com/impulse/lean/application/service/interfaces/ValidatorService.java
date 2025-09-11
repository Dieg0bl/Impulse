package com.impulse.lean.application.service.interfaces;

import com.impulse.lean.application.dto.validator.ValidatorAssignmentRequestDto;
import com.impulse.lean.application.dto.validator.ValidatorAssignmentResponseDto;
import com.impulse.lean.application.dto.validator.ValidatorRequestDto;
import com.impulse.lean.application.dto.validator.ValidatorResponseDto;
import com.impulse.lean.domain.model.Evidence;
import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.model.Validator;
import com.impulse.lean.domain.model.ValidatorAssignment;
import com.impulse.lean.domain.model.ValidatorSpecialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * IMPULSE LEAN v1 - Validator Service Interface
 * 
 * Service for validator management and assignment operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public interface ValidatorService {

    // Validator CRUD operations
    Optional<Validator> findById(Long id);
    Optional<Validator> findByUuid(String uuid);
    Optional<Validator> findByUser(User user);
    Validator save(Validator validator);
    void deleteById(Long id);

    // Validator registration and management
    Validator registerValidator(User user, ValidatorRequestDto request);
    Validator updateValidator(String uuid, ValidatorRequestDto request);
    Validator deactivateValidator(String uuid);
    Validator activateValidator(String uuid);

    // Validator assignment operations
    ValidatorAssignment assignValidator(String evidenceUuid, String validatorUuid, ValidatorAssignmentRequestDto request);
    ValidatorAssignment reassignValidator(String assignmentUuid, String newValidatorUuid, String reason);
    ValidatorAssignment unassignValidator(String assignmentUuid, String reason);

    // Validator qualification and specialties
    Validator addSpecialty(String validatorUuid, ValidatorSpecialty specialty);
    Validator removeSpecialty(String validatorUuid, ValidatorSpecialty specialty);
    boolean hasSpecialty(String validatorUuid, ValidatorSpecialty specialty);
    List<ValidatorSpecialty> getValidatorSpecialties(String validatorUuid);

    // Validator availability and workload
    boolean isValidatorAvailable(String validatorUuid);
    int getValidatorWorkload(String validatorUuid);
    void setValidatorAvailability(String validatorUuid, boolean available);
    void updateValidatorCapacity(String validatorUuid, int maxAssignments);

    // Validator queries
    List<Validator> findValidatorsBySpecialty(ValidatorSpecialty specialty);
    List<Validator> findAvailableValidators();
    List<Validator> findValidatorsForEvidence(String evidenceUuid);
    Page<Validator> findValidators(Pageable pageable);
    Page<Validator> searchValidators(String searchTerm, Pageable pageable);

    // Validator assignment queries
    List<ValidatorAssignment> findAssignmentsByValidator(String validatorUuid);
    List<ValidatorAssignment> findAssignmentsByEvidence(String evidenceUuid);
    List<ValidatorAssignment> findPendingAssignments(String validatorUuid);
    List<ValidatorAssignment> findOverdueAssignments(LocalDateTime threshold);
    Optional<ValidatorAssignment> findActiveAssignment(String evidenceUuid, String validatorUuid);

    // Validator recommendation and matching
    List<Validator> findBestValidatorsForEvidence(String evidenceUuid, int limit);
    Validator findOptimalValidator(String evidenceUuid);
    List<Validator> findValidatorsByExpertise(String domain);
    List<Validator> findValidatorsByRating(BigDecimal minRating);

    // Validator performance metrics
    BigDecimal getValidatorRating(String validatorUuid);
    int getValidatorTotalValidations(String validatorUuid);
    int getValidatorPendingValidations(String validatorUuid);
    double getValidatorAverageResponseTime(String validatorUuid);
    double getValidatorAccuracyScore(String validatorUuid);

    // Validator notifications and workflow
    void notifyValidatorAssignment(String validatorUuid, String evidenceUuid);
    void notifyValidatorDeadline(String validatorUuid, String evidenceUuid);
    void sendReminderNotification(String validatorUuid, String evidenceUuid);
    void notifyValidatorAssignmentUpdate(String validatorUuid, String assignmentUuid, String updateType);

    // Validator auto-assignment
    ValidatorAssignment autoAssignValidator(String evidenceUuid);
    List<ValidatorAssignment> autoAssignValidators(List<String> evidenceUuids);
    void enableAutoAssignment(boolean enabled);
    boolean isAutoAssignmentEnabled();

    // Validator feedback and reporting
    void recordValidatorFeedback(String validatorUuid, String evidenceUuid, String feedback, int rating);
    List<String> getValidatorFeedback(String validatorUuid);
    void reportValidatorIssue(String validatorUuid, String reporterUuid, String issue);
    List<String> getValidatorIssues(String validatorUuid);

    // Validator statistics and analytics
    long countValidatorsBySpecialty(ValidatorSpecialty specialty);
    long countActiveValidators();
    long countAvailableValidators();
    BigDecimal getAverageValidatorRating();
    double getAverageAssignmentTime();

    // Validator capacity management
    boolean canValidatorAcceptAssignment(String validatorUuid);
    void adjustValidatorCapacity(String validatorUuid, int capacityChange);
    List<Validator> findOverloadedValidators();
    List<Validator> findUnderutilizedValidators();

    // Validator certification and training
    void certifyValidator(String validatorUuid, ValidatorSpecialty specialty, String certificateId);
    void revokeValidatorCertification(String validatorUuid, ValidatorSpecialty specialty);
    boolean isValidatorCertified(String validatorUuid, ValidatorSpecialty specialty);
    LocalDateTime getValidatorCertificationExpiry(String validatorUuid, ValidatorSpecialty specialty);

    // Validator batch operations
    List<ValidatorAssignment> bulkAssignValidators(List<String> evidenceUuids, String validatorUuid);
    void bulkUpdateValidatorStatus(List<String> validatorUuids, boolean active);
    void processExpiredAssignments(LocalDateTime threshold);
    void cleanupInactiveValidators(LocalDateTime threshold);
}
