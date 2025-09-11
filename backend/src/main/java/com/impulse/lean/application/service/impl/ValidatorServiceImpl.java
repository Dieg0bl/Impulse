package com.impulse.lean.application.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impulse.lean.application.dto.validator.ValidatorAssignmentRequestDto;
import com.impulse.lean.application.dto.validator.ValidatorRequestDto;
import com.impulse.lean.application.service.interfaces.ValidatorService;
import com.impulse.lean.domain.model.AssignmentStatus;
import com.impulse.lean.domain.model.CertificationLevel;
import com.impulse.lean.domain.model.Evidence;
import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.model.ValidationPriority;
import com.impulse.lean.domain.model.Validator;
import com.impulse.lean.domain.model.ValidatorAssignment;
import com.impulse.lean.domain.model.ValidatorSpecialty;
import com.impulse.lean.domain.model.ValidatorStatus;
import com.impulse.lean.domain.repository.EvidenceRepository;
import com.impulse.lean.domain.repository.UserRepository;
import com.impulse.lean.domain.repository.ValidatorAssignmentRepository;
import com.impulse.lean.domain.repository.ValidatorRepository;

/**
 * IMPULSE LEAN v1 - Validator Service Implementation
 * 
 * Service implementation for validator management and assignment operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Service
@Transactional
public class ValidatorServiceImpl implements ValidatorService {

    @Autowired
    private ValidatorRepository validatorRepository;

    @Autowired
    private ValidatorAssignmentRepository validatorAssignmentRepository;

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private UserRepository userRepository;

    // Validator CRUD operations
    @Override
    @Transactional(readOnly = true)
    public Optional<Validator> findById(Long id) {
        return validatorRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Validator> findByUuid(String uuid) {
        return validatorRepository.findByUuid(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Validator> findByUser(User user) {
        return validatorRepository.findByUser(user);
    }

    @Override
    public Validator save(Validator validator) {
        return validatorRepository.save(validator);
    }

    @Override
    public void deleteById(Long id) {
        validatorRepository.deleteById(id);
    }

    // Validator registration and management
    @Override
    public Validator registerValidator(User user, ValidatorRequestDto request) {
        // Check if user is already a validator
        Optional<Validator> existingValidator = validatorRepository.findByUser(user);
        if (existingValidator.isPresent()) {
            throw new IllegalArgumentException("User is already registered as validator");
        }

        Validator validator = new Validator(user);
        validator.setStatus(ValidatorStatus.PENDING_APPROVAL);
        validator.setBio(request.getBio());
        validator.setExpertise(request.getExpertise());
        validator.setMaxAssignments(request.getMaxAssignments() != null ? request.getMaxAssignments() : 5);
        validator.setCertificationLevel(request.getCertificationLevel() != null ? 
            request.getCertificationLevel() : CertificationLevel.BASIC);

        if (request.getSpecialties() != null && !request.getSpecialties().isEmpty()) {
            validator.setSpecialties(request.getSpecialties());
        }

        return validatorRepository.save(validator);
    }

    @Override
    public Validator updateValidator(String uuid, ValidatorRequestDto request) {
        Validator validator = validatorRepository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found"));

        if (request.getBio() != null) {
            validator.setBio(request.getBio());
        }
        if (request.getExpertise() != null) {
            validator.setExpertise(request.getExpertise());
        }
        if (request.getMaxAssignments() != null) {
            validator.setMaxAssignments(request.getMaxAssignments());
        }
        if (request.getSpecialties() != null) {
            validator.setSpecialties(request.getSpecialties());
        }
        if (request.getAvailable() != null) {
            validator.setAvailable(request.getAvailable());
        }

        validator.updateActivity();
        return validatorRepository.save(validator);
    }

    @Override
    public Validator deactivateValidator(String uuid) {
        Validator validator = validatorRepository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found"));

        validator.setStatus(ValidatorStatus.INACTIVE);
        validator.setAvailable(false);
        
        // Cancel active assignments
        List<ValidatorAssignment> activeAssignments = validatorAssignmentRepository
            .findActiveAssignmentsByValidator(uuid);
        for (ValidatorAssignment assignment : activeAssignments) {
            assignment.cancel("Validator deactivated");
            validatorAssignmentRepository.save(assignment);
        }

        return validatorRepository.save(validator);
    }

    @Override
    public Validator activateValidator(String uuid) {
        Validator validator = validatorRepository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found"));

        validator.setStatus(ValidatorStatus.ACTIVE);
        validator.setAvailable(true);
        validator.updateActivity();
        
        return validatorRepository.save(validator);
    }

    // Validator assignment operations
    @Override
    public ValidatorAssignment assignValidator(String evidenceUuid, String validatorUuid, 
                                               ValidatorAssignmentRequestDto request) {
        Evidence evidence = evidenceRepository.findByUuid(evidenceUuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found"));
        
        Validator validator = validatorRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found"));

        // Check if validator can accept assignment
        if (!validator.canAcceptAssignment()) {
            throw new IllegalStateException("Validator cannot accept new assignments");
        }

        // Check for existing active assignment
        Optional<ValidatorAssignment> existingAssignment = validatorAssignmentRepository
            .findActiveAssignmentByValidatorAndEvidence(validatorUuid, evidenceUuid);
        if (existingAssignment.isPresent()) {
            throw new IllegalStateException("Validator already assigned to this evidence");
        }

        // Create assignment
        ValidatorAssignment assignment = new ValidatorAssignment(validator, evidence, 
            getCurrentUser()); // You'll need to implement getCurrentUser()

        if (request.getPriority() != null) {
            assignment.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            assignment.setDueDate(request.getDueDate());
        }
        if (request.getAssignmentReason() != null) {
            assignment.setAssignmentReason(request.getAssignmentReason());
        }
        if (request.getNotes() != null) {
            assignment.setNotes(request.getNotes());
        }
        if (request.getAutoAssigned() != null) {
            assignment.setAutoAssigned(request.getAutoAssigned());
        }

        // Update validator workload
        validator.incrementAssignments();
        validatorRepository.save(validator);

        ValidatorAssignment savedAssignment = validatorAssignmentRepository.save(assignment);

        // Send notification if requested
        if (request.getSendNotification() == null || request.getSendNotification()) {
            notifyValidatorAssignment(validatorUuid, evidenceUuid);
        }

        return savedAssignment;
    }

    @Override
    public ValidatorAssignment reassignValidator(String assignmentUuid, String newValidatorUuid, String reason) {
        ValidatorAssignment assignment = validatorAssignmentRepository.findByUuid(assignmentUuid)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!assignment.isActive()) {
            throw new IllegalStateException("Cannot reassign completed or cancelled assignment");
        }

        Validator newValidator = validatorRepository.findByUuid(newValidatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("New validator not found"));

        if (!newValidator.canAcceptAssignment()) {
            throw new IllegalStateException("New validator cannot accept assignments");
        }

        // Update old validator workload
        Validator oldValidator = assignment.getValidator();
        oldValidator.decrementAssignments();
        validatorRepository.save(oldValidator);

        // Update assignment
        assignment.setValidator(newValidator);
        assignment.setStatus(AssignmentStatus.ASSIGNED);
        assignment.setAssignmentReason(reason);
        assignment.setAcceptedAt(null);
        assignment.setStartedAt(null);

        // Update new validator workload
        newValidator.incrementAssignments();
        validatorRepository.save(newValidator);

        ValidatorAssignment savedAssignment = validatorAssignmentRepository.save(assignment);

        // Notify new validator
        notifyValidatorAssignment(newValidatorUuid, assignment.getEvidence().getUuid());

        return savedAssignment;
    }

    @Override
    public ValidatorAssignment unassignValidator(String assignmentUuid, String reason) {
        ValidatorAssignment assignment = validatorAssignmentRepository.findByUuid(assignmentUuid)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!assignment.isActive()) {
            throw new IllegalStateException("Cannot unassign completed or cancelled assignment");
        }

        // Update validator workload
        Validator validator = assignment.getValidator();
        validator.decrementAssignments();
        validatorRepository.save(validator);

        // Cancel assignment
        assignment.cancel(reason);
        return validatorAssignmentRepository.save(assignment);
    }

    // Validator qualification and specialties
    @Override
    public Validator addSpecialty(String validatorUuid, ValidatorSpecialty specialty) {
        Validator validator = validatorRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found"));

        validator.addSpecialty(specialty);
        return validatorRepository.save(validator);
    }

    @Override
    public Validator removeSpecialty(String validatorUuid, ValidatorSpecialty specialty) {
        Validator validator = validatorRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found"));

        validator.removeSpecialty(specialty);
        return validatorRepository.save(validator);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasSpecialty(String validatorUuid, ValidatorSpecialty specialty) {
        Validator validator = validatorRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found"));

        return validator.hasSpecialty(specialty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValidatorSpecialty> getValidatorSpecialties(String validatorUuid) {
        Validator validator = validatorRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found"));

        return new ArrayList<>(validator.getSpecialties());
    }

    // Validator availability and workload
    @Override
    @Transactional(readOnly = true)
    public boolean isValidatorAvailable(String validatorUuid) {
        Validator validator = validatorRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found"));

        return validator.canAcceptAssignment();
    }

    @Override
    @Transactional(readOnly = true)
    public int getValidatorWorkload(String validatorUuid) {
        return (int) validatorAssignmentRepository.countActiveAssignmentsByValidator(validatorUuid);
    }

    @Override
    public void setValidatorAvailability(String validatorUuid, boolean available) {
        Validator validator = validatorRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found"));

        validator.setAvailable(available);
        validator.updateActivity();
        validatorRepository.save(validator);
    }

    @Override
    public void updateValidatorCapacity(String validatorUuid, int maxAssignments) {
        Validator validator = validatorRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found"));

        validator.setMaxAssignments(maxAssignments);
        validatorRepository.save(validator);
    }

    // Validator queries
    @Override
    @Transactional(readOnly = true)
    public List<Validator> findValidatorsBySpecialty(ValidatorSpecialty specialty) {
        return validatorRepository.findBySpecialty(specialty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Validator> findAvailableValidators() {
        return validatorRepository.findAvailableValidators();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Validator> findValidatorsForEvidence(String evidenceUuid) {
        Evidence evidence = evidenceRepository.findByUuid(evidenceUuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found"));

        // For now, return all available validators
        // TODO: Implement logic to match validators based on evidence type/requirements
        return validatorRepository.findValidatorsAcceptingAssignments();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Validator> findValidators(Pageable pageable) {
        return validatorRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Validator> searchValidators(String searchTerm, Pageable pageable) {
        return validatorRepository.searchValidators(searchTerm, pageable);
    }

    // Validator assignment queries
    @Override
    @Transactional(readOnly = true)
    public List<ValidatorAssignment> findAssignmentsByValidator(String validatorUuid) {
        return validatorAssignmentRepository.findByValidatorUuid(validatorUuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValidatorAssignment> findAssignmentsByEvidence(String evidenceUuid) {
        return validatorAssignmentRepository.findByEvidenceUuid(evidenceUuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValidatorAssignment> findPendingAssignments(String validatorUuid) {
        return validatorAssignmentRepository.findPendingAssignmentsByValidator(validatorUuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValidatorAssignment> findOverdueAssignments(LocalDateTime threshold) {
        return validatorAssignmentRepository.findOverdueAssignments();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ValidatorAssignment> findActiveAssignment(String evidenceUuid, String validatorUuid) {
        return validatorAssignmentRepository.findActiveAssignmentByValidatorAndEvidence(validatorUuid, evidenceUuid);
    }

    // Validator recommendation and matching
    @Override
    @Transactional(readOnly = true)
    public List<Validator> findBestValidatorsForEvidence(String evidenceUuid, int limit) {
        Evidence evidence = evidenceRepository.findByUuid(evidenceUuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found"));

        // Basic implementation - find available validators with good ratings
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "rating"));
        return validatorRepository.findOptimalValidators(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Validator findOptimalValidator(String evidenceUuid) {
        List<Validator> bestValidators = findBestValidatorsForEvidence(evidenceUuid, 1);
        return bestValidators.isEmpty() ? null : bestValidators.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Validator> findValidatorsByExpertise(String domain) {
        return validatorRepository.findByExpertiseDomain(domain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Validator> findValidatorsByRating(BigDecimal minRating) {
        return validatorRepository.findByMinRating(minRating);
    }

    // Placeholder methods for notification system (to be implemented later)
    @Override
    public void notifyValidatorAssignment(String validatorUuid, String evidenceUuid) {
        // TODO: Implement notification system
        System.out.println("Notification: Validator " + validatorUuid + " assigned to evidence " + evidenceUuid);
    }

    @Override
    public void notifyValidatorDeadline(String validatorUuid, String evidenceUuid) {
        // TODO: Implement notification system
        System.out.println("Deadline notification: Validator " + validatorUuid + " for evidence " + evidenceUuid);
    }

    @Override
    public void sendReminderNotification(String validatorUuid, String evidenceUuid) {
        // TODO: Implement notification system
        System.out.println("Reminder: Validator " + validatorUuid + " for evidence " + evidenceUuid);
    }

    @Override
    public void notifyValidatorAssignmentUpdate(String validatorUuid, String assignmentUuid, String updateType) {
        // TODO: Implement notification system
        System.out.println("Update notification: " + updateType + " for assignment " + assignmentUuid);
    }

    // Auto-assignment (basic implementation)
    @Override
    public ValidatorAssignment autoAssignValidator(String evidenceUuid) {
        Validator optimalValidator = findOptimalValidator(evidenceUuid);
        if (optimalValidator == null) {
            throw new IllegalStateException("No available validators found for evidence");
        }

        ValidatorAssignmentRequestDto request = new ValidatorAssignmentRequestDto();
        request.setPriority(ValidationPriority.NORMAL);
        request.setAutoAssigned(true);
        request.setAssignmentReason("Auto-assigned by system");

        return assignValidator(evidenceUuid, optimalValidator.getUuid(), request);
    }

    @Override
    public List<ValidatorAssignment> autoAssignValidators(List<String> evidenceUuids) {
        List<ValidatorAssignment> assignments = new ArrayList<>();
        for (String evidenceUuid : evidenceUuids) {
            try {
                ValidatorAssignment assignment = autoAssignValidator(evidenceUuid);
                assignments.add(assignment);
            } catch (Exception e) {
                // Log error and continue with next evidence
                System.err.println("Failed to auto-assign validator for evidence " + evidenceUuid + ": " + e.getMessage());
            }
        }
        return assignments;
    }

    @Override
    public void enableAutoAssignment(boolean enabled) {
        // TODO: Implement global auto-assignment setting
        System.out.println("Auto-assignment " + (enabled ? "enabled" : "disabled"));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAutoAssignmentEnabled() {
        // TODO: Implement global auto-assignment setting
        return true; // Default enabled
    }

    // Statistics methods (basic implementations)
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getValidatorRating(String validatorUuid) {
        Validator validator = validatorRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found"));
        return validator.getRating();
    }

    @Override
    @Transactional(readOnly = true)
    public int getValidatorTotalValidations(String validatorUuid) {
        Validator validator = validatorRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found"));
        return validator.getTotalValidations();
    }

    @Override
    @Transactional(readOnly = true)
    public int getValidatorPendingValidations(String validatorUuid) {
        return findPendingAssignments(validatorUuid).size();
    }

    @Override
    @Transactional(readOnly = true)
    public double getValidatorAverageResponseTime(String validatorUuid) {
        Validator validator = validatorRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found"));
        return validator.getAverageResponseTimeHours() != null ? 
            validator.getAverageResponseTimeHours().doubleValue() : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public double getValidatorAccuracyScore(String validatorUuid) {
        Validator validator = validatorRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found"));
        return validator.getSuccessRate();
    }

    @Override
    @Transactional(readOnly = true)
    public long countValidatorsBySpecialty(ValidatorSpecialty specialty) {
        return validatorRepository.findBySpecialty(specialty).size();
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveValidators() {
        return validatorRepository.countByStatus(ValidatorStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAvailableValidators() {
        return validatorRepository.countAvailableValidators();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAverageValidatorRating() {
        return validatorRepository.getAverageRating();
    }

    @Override
    @Transactional(readOnly = true)
    public double getAverageAssignmentTime() {
        Double avgTime = validatorAssignmentRepository.getAverageCompletionTimeHours();
        return avgTime != null ? avgTime : 0.0;
    }

    // Capacity management
    @Override
    @Transactional(readOnly = true)
    public boolean canValidatorAcceptAssignment(String validatorUuid) {
        return isValidatorAvailable(validatorUuid);
    }

    @Override
    public void adjustValidatorCapacity(String validatorUuid, int capacityChange) {
        Validator validator = validatorRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found"));

        int newCapacity = Math.max(1, validator.getMaxAssignments() + capacityChange);
        validator.setMaxAssignments(newCapacity);
        validatorRepository.save(validator);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Validator> findOverloadedValidators() {
        return validatorRepository.findOverloadedValidators();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Validator> findUnderutilizedValidators() {
        return validatorRepository.findUnderutilizedValidators();
    }

    // Placeholder implementations for remaining methods
    @Override
    public void recordValidatorFeedback(String validatorUuid, String evidenceUuid, String feedback, int rating) {
        // TODO: Implement feedback system
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getValidatorFeedback(String validatorUuid) {
        // TODO: Implement feedback system
        return new ArrayList<>();
    }

    @Override
    public void reportValidatorIssue(String validatorUuid, String reporterUuid, String issue) {
        // TODO: Implement issue reporting system
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getValidatorIssues(String validatorUuid) {
        // TODO: Implement issue reporting system
        return new ArrayList<>();
    }

    @Override
    public void certifyValidator(String validatorUuid, ValidatorSpecialty specialty, String certificateId) {
        // TODO: Implement certification system
    }

    @Override
    public void revokeValidatorCertification(String validatorUuid, ValidatorSpecialty specialty) {
        // TODO: Implement certification system
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValidatorCertified(String validatorUuid, ValidatorSpecialty specialty) {
        // TODO: Implement certification system
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDateTime getValidatorCertificationExpiry(String validatorUuid, ValidatorSpecialty specialty) {
        // TODO: Implement certification system
        return null;
    }

    @Override
    public List<ValidatorAssignment> bulkAssignValidators(List<String> evidenceUuids, String validatorUuid) {
        // TODO: Implement bulk operations
        return new ArrayList<>();
    }

    @Override
    public void bulkUpdateValidatorStatus(List<String> validatorUuids, boolean active) {
        // TODO: Implement bulk operations
    }

    @Override
    public void processExpiredAssignments(LocalDateTime threshold) {
        // TODO: Implement expired assignment processing
    }

    @Override
    public void cleanupInactiveValidators(LocalDateTime threshold) {
        // TODO: Implement cleanup operations
    }

    // Helper method to get current user (needs to be implemented)
    private User getCurrentUser() {
        // TODO: Get current user from security context
        // For now, return a dummy user or throw exception
        throw new UnsupportedOperationException("getCurrentUser not implemented yet");
    }
}
