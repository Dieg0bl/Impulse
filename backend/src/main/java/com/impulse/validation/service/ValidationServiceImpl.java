package com.impulse.validation.service;

import com.impulse.lean.application.dto.validation.EvidenceValidationRequestDto;
import com.impulse.lean.application.service.interfaces.ValidationService;
import com.impulse.lean.domain.model.*;
import com.impulse.lean.domain.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * IMPULSE LEAN v1 - Validation Service Implementation
 * 
 * Service implementation for evidence validation operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Service
@Transactional
public class ValidationServiceImpl implements ValidationService {

    @Autowired
    private EvidenceValidationRepository validationRepository;

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidatorRepository validatorRepository;

    private static final BigDecimal POSITIVE_THRESHOLD = new BigDecimal("0.70");
    private static final BigDecimal HIGH_CONFIDENCE_THRESHOLD = new BigDecimal("0.85");

    @Override
    public EvidenceValidation createValidation(String evidenceUuid, String validatorUuid, EvidenceValidationRequestDto request) {
        Evidence evidence = evidenceRepository.findByUuid(evidenceUuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + evidenceUuid));

        User validator = userRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found: " + validatorUuid));

        // Check if validation already exists
        Optional<EvidenceValidation> existingValidation = validationRepository
            .findByEvidenceAndValidatorAndValidationType(evidence, validator, ValidationType.MANUAL);
        
        if (existingValidation.isPresent()) {
            throw new IllegalStateException("Validation already exists for this evidence and validator");
        }

        EvidenceValidation validation = new EvidenceValidation();
        validation.setEvidence(evidence);
        validation.setValidator(validator);
        validation.setValidationType(ValidationType.MANUAL);
        validation.setScore(BigDecimal.ZERO); // Will be updated when completed
        validation.setConfidenceLevel(new BigDecimal("0.50")); // Default medium confidence

        return validationRepository.save(validation);
    }

    @Override
    public EvidenceValidation startValidation(String validationUuid) {
        // For this simplified model, we don't have separate start/complete states
        // The validation is created when assigned and completed when scored
        return findByUuid(validationUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validation not found: " + validationUuid));
    }

    @Override
    public EvidenceValidation completeValidation(String validationUuid, BigDecimal score, String feedback) {
        EvidenceValidation validation = findByUuid(validationUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validation not found: " + validationUuid));

        if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Score must be between 0.00 and 1.00");
        }

        validation.setScore(score);
        validation.setFeedback(feedback);
        validation.setValidatedAt(LocalDateTime.now());

        // Set confidence level based on score
        if (score.compareTo(HIGH_CONFIDENCE_THRESHOLD) >= 0 || score.compareTo(new BigDecimal("0.30")) <= 0) {
            validation.setConfidenceLevel(new BigDecimal("0.90")); // High confidence for clear decisions
        } else {
            validation.setConfidenceLevel(new BigDecimal("0.70")); // Medium confidence for borderline cases
        }

        EvidenceValidation savedValidation = validationRepository.save(validation);

        // Update evidence status based on validation result
        updateEvidenceStatusAfterValidation(validation.getEvidence(), score);

        return savedValidation;
    }

    @Override
    public EvidenceValidation updateValidation(String validationUuid, EvidenceValidationRequestDto request) {
        EvidenceValidation validation = findByUuid(validationUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validation not found: " + validationUuid));

        if (request.getComments() != null) {
            validation.setFeedback(request.getComments());
        }

        return validationRepository.save(validation);
    }

    @Override
    public void deleteValidation(String validationUuid) {
        EvidenceValidation validation = findByUuid(validationUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validation not found: " + validationUuid));

        validationRepository.delete(validation);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EvidenceValidation> findByUuid(String uuid) {
        return validationRepository.findById(Long.parseLong(uuid));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenceValidation> findByEvidence(String evidenceUuid) {
        Evidence evidence = evidenceRepository.findByUuid(evidenceUuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + evidenceUuid));
        
        return validationRepository.findByEvidence(evidence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenceValidation> findByValidator(String validatorUuid) {
        User validator = userRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found: " + validatorUuid));
        
        return validationRepository.findByValidator(validator);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EvidenceValidation> findValidations(Pageable pageable) {
        return validationRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EvidenceValidation> searchValidations(String searchTerm, Pageable pageable) {
        return validationRepository.findByFeedbackContainingIgnoreCase(searchTerm, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenceValidation> findPendingValidations() {
        // For this model, pending validations are those with score = 0 and no feedback
        return validationRepository.findAll().stream()
            .filter(v -> v.getScore().equals(BigDecimal.ZERO) && 
                        (v.getFeedback() == null || v.getFeedback().trim().isEmpty()))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenceValidation> findCompletedValidations() {
        return validationRepository.findAll().stream()
            .filter(v -> !v.getScore().equals(BigDecimal.ZERO) || 
                        (v.getFeedback() != null && !v.getFeedback().trim().isEmpty()))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenceValidation> findValidationsByType(ValidationType type) {
        return validationRepository.findByValidationType(type);
    }

    @Override
    public EvidenceValidation assignValidation(String evidenceUuid, String validatorUuid, ValidationType type) {
        Evidence evidence = evidenceRepository.findByUuid(evidenceUuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + evidenceUuid));

        User validator = userRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found: " + validatorUuid));

        EvidenceValidation validation = new EvidenceValidation(evidence, validator, type, BigDecimal.ZERO);
        validation.setConfidenceLevel(new BigDecimal("0.50"));

        return validationRepository.save(validation);
    }

    @Override
    public void reassignValidation(String validationUuid, String newValidatorUuid) {
        EvidenceValidation validation = findByUuid(validationUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validation not found: " + validationUuid));

        User newValidator = userRepository.findByUuid(newValidatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("New validator not found: " + newValidatorUuid));

        validation.setValidator(newValidator);
        validation.setScore(BigDecimal.ZERO); // Reset score for new validator
        validation.setFeedback(null); // Reset feedback
        validation.setValidatedAt(LocalDateTime.now());

        validationRepository.save(validation);
    }

    @Override
    public void escalateValidation(String validationUuid, String reason) {
        EvidenceValidation validation = findByUuid(validationUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validation not found: " + validationUuid));

        String escalationNote = "ESCALATED: " + reason;
        if (validation.getFeedback() != null) {
            validation.setFeedback(validation.getFeedback() + "\n\n" + escalationNote);
        } else {
            validation.setFeedback(escalationNote);
        }

        validationRepository.save(validation);
    }

    @Override
    public EvidenceValidation performAutomaticValidation(String evidenceUuid) {
        Evidence evidence = evidenceRepository.findByUuid(evidenceUuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + evidenceUuid));

        // Simple automatic validation logic
        BigDecimal autoScore = calculateAutomaticScore(evidence);
        String autoFeedback = generateAutomaticFeedback(evidence, autoScore);

        EvidenceValidation validation = new EvidenceValidation();
        validation.setEvidence(evidence);
        validation.setValidator(null); // No human validator for automatic
        validation.setValidationType(ValidationType.AUTOMATIC);
        validation.setScore(autoScore);
        validation.setFeedback(autoFeedback);
        validation.setConfidenceLevel(new BigDecimal("0.60")); // Medium confidence for auto
        validation.setValidatedAt(LocalDateTime.now());

        EvidenceValidation savedValidation = validationRepository.save(validation);
        updateEvidenceStatusAfterValidation(evidence, autoScore);

        return savedValidation;
    }

    @Override
    public List<EvidenceValidation> processAutomaticValidations() {
        // Find evidence that needs automatic validation
        List<Evidence> evidenceNeedingValidation = evidenceRepository.findByStatus(EvidenceStatus.SUBMITTED)
            .stream()
            .filter(e -> !hasValidation(e))
            .collect(Collectors.toList());

        return evidenceNeedingValidation.stream()
            .map(e -> performAutomaticValidation(e.getUuid()))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAverageValidationScore() {
        return validationRepository.getAverageScore();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAverageValidationScoreByValidator(String validatorUuid) {
        User validator = userRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found: " + validatorUuid));
        
        return validationRepository.getAverageScoreByValidator(validator);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAverageValidationScoreByEvidence(String evidenceUuid) {
        Evidence evidence = evidenceRepository.findByUuid(evidenceUuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + evidenceUuid));
        
        return validationRepository.getAverageScoreByEvidence(evidence);
    }

    @Override
    @Transactional(readOnly = true)
    public long getValidationCount() {
        return validationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getValidationCountByValidator(String validatorUuid) {
        User validator = userRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found: " + validatorUuid));
        
        return validationRepository.countByValidator(validator);
    }

    @Override
    @Transactional(readOnly = true)
    public long getValidationCountByType(ValidationType type) {
        return validationRepository.countByValidationType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenceValidation> getValidationsInDateRange(LocalDateTime start, LocalDateTime end) {
        return validationRepository.findByValidatedAtBetween(start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenceValidation> getHighScoreValidations(BigDecimal minScore) {
        return validationRepository.findByScoreGreaterThanEqual(minScore);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenceValidation> getLowScoreValidations(BigDecimal maxScore) {
        return validationRepository.findByScoreLessThanEqual(maxScore);
    }

    @Override
    @Transactional(readOnly = true)
    public double getValidationAccuracy(String validatorUuid) {
        User validator = userRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found: " + validatorUuid));
        
        List<EvidenceValidation> validations = validationRepository.findByValidator(validator);
        if (validations.isEmpty()) {
            return 0.0;
        }

        long accurateValidations = validations.stream()
            .filter(v -> v.getConfidenceLevel().compareTo(HIGH_CONFIDENCE_THRESHOLD) >= 0)
            .count();

        return (double) accurateValidations / validations.size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenceValidation> findInconsistentValidations() {
        // Find validations with low confidence or contradictory scores
        return validationRepository.findAll().stream()
            .filter(v -> v.getConfidenceLevel().compareTo(new BigDecimal("0.50")) < 0)
            .collect(Collectors.toList());
    }

    @Override
    public void flagValidationForReview(String validationUuid, String reason) {
        EvidenceValidation validation = findByUuid(validationUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validation not found: " + validationUuid));

        String flagNote = "FLAGGED FOR REVIEW: " + reason;
        if (validation.getFeedback() != null) {
            validation.setFeedback(validation.getFeedback() + "\n\n" + flagNote);
        } else {
            validation.setFeedback(flagNote);
        }

        validation.setConfidenceLevel(new BigDecimal("0.30")); // Low confidence when flagged

        validationRepository.save(validation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getRecommendedValidators(String evidenceUuid) {
        Evidence evidence = evidenceRepository.findByUuid(evidenceUuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + evidenceUuid));

        // Simple recommendation: return validators with high average scores
        return validatorRepository.findActiveValidators().stream()
            .map(Validator::getUser)
            .filter(user -> {
                BigDecimal avgScore = getAverageValidationScoreByValidator(user.getUuid());
                return avgScore != null && avgScore.compareTo(POSITIVE_THRESHOLD) >= 0;
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ValidationType getRecommendedValidationType(String evidenceUuid) {
        Evidence evidence = evidenceRepository.findByUuid(evidenceUuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + evidenceUuid));

        // Simple logic: recommend PEER for most cases, EXPERT for complex evidence
        if (evidence.getType() == EvidenceType.RESEARCH_PUBLICATION || 
            evidence.getType() == EvidenceType.TECHNICAL_SPECIFICATION) {
            return ValidationType.EXPERT;
        }
        return ValidationType.PEER;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal predictValidationScore(String evidenceUuid, String validatorUuid) {
        // Simple prediction based on validator's average score
        BigDecimal avgScore = getAverageValidationScoreByValidator(validatorUuid);
        if (avgScore == null) {
            return new BigDecimal("0.70"); // Default prediction
        }
        return avgScore;
    }

    @Override
    public List<EvidenceValidation> createBulkValidations(List<String> evidenceUuids, String validatorUuid, ValidationType type) {
        return evidenceUuids.stream()
            .map(evidenceUuid -> assignValidation(evidenceUuid, validatorUuid, type))
            .collect(Collectors.toList());
    }

    @Override
    public void processBulkValidations(List<String> validationUuids, BigDecimal score, String feedback) {
        validationUuids.forEach(uuid -> completeValidation(uuid, score, feedback));
    }

    @Override
    public void deleteValidationsByEvidence(String evidenceUuid) {
        Evidence evidence = evidenceRepository.findByUuid(evidenceUuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + evidenceUuid));
        
        List<EvidenceValidation> validations = validationRepository.findByEvidence(evidence);
        validationRepository.deleteAll(validations);
    }

    @Override
    public void deleteValidationsByValidator(String validatorUuid) {
        User validator = userRepository.findByUuid(validatorUuid)
            .orElseThrow(() -> new IllegalArgumentException("Validator not found: " + validatorUuid));
        
        List<EvidenceValidation> validations = validationRepository.findByValidator(validator);
        validationRepository.deleteAll(validations);
    }

    // Private helper methods
    private boolean hasValidation(Evidence evidence) {
        return !validationRepository.findByEvidence(evidence).isEmpty();
    }

    private BigDecimal calculateAutomaticScore(Evidence evidence) {
        // Simple automatic scoring based on evidence completeness
        Random random = new Random();
        double baseScore = 0.60; // Base score
        
        // Add points for having attachments
        if (evidence.getAttachments() != null && !evidence.getAttachments().isEmpty()) {
            baseScore += 0.20;
        }
        
        // Add points for having description
        if (evidence.getDescription() != null && evidence.getDescription().length() > 100) {
            baseScore += 0.15;
        }
        
        // Add small random variation
        baseScore += (random.nextDouble() - 0.5) * 0.10;
        
        // Ensure score is within bounds
        baseScore = Math.max(0.0, Math.min(1.0, baseScore));
        
        return BigDecimal.valueOf(baseScore).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private String generateAutomaticFeedback(Evidence evidence, BigDecimal score) {
        StringBuilder feedback = new StringBuilder("Automatic validation completed. ");
        
        if (score.compareTo(POSITIVE_THRESHOLD) >= 0) {
            feedback.append("Evidence meets quality standards. ");
        } else {
            feedback.append("Evidence may need improvement. ");
        }
        
        if (evidence.getAttachments() == null || evidence.getAttachments().isEmpty()) {
            feedback.append("Consider adding supporting attachments. ");
        }
        
        if (evidence.getDescription() == null || evidence.getDescription().length() < 100) {
            feedback.append("Consider providing a more detailed description. ");
        }
        
        feedback.append("Score: ").append(score);
        
        return feedback.toString();
    }

    private void updateEvidenceStatusAfterValidation(Evidence evidence, BigDecimal score) {
        if (score.compareTo(POSITIVE_THRESHOLD) >= 0) {
            evidence.setStatus(EvidenceStatus.APPROVED);
        } else {
            evidence.setStatus(EvidenceStatus.REJECTED);
        }
        evidenceRepository.save(evidence);
    }
}
