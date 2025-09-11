package com.impulse.lean.application.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impulse.lean.application.dto.evidence.EvidenceCreateRequestDto;
import com.impulse.lean.application.dto.evidence.EvidenceUpdateRequestDto;
import com.impulse.lean.application.dto.evidence.EvidenceValidationRequestDto;
import com.impulse.lean.application.service.interfaces.EvidenceService;
import com.impulse.lean.domain.model.ChallengeParticipation;
import com.impulse.lean.domain.model.Evidence;
import com.impulse.lean.domain.model.EvidenceStatus;
import com.impulse.lean.domain.model.EvidenceType;
import com.impulse.lean.domain.model.EvidenceValidation;
import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.model.ValidationType;
import com.impulse.lean.domain.repository.EvidenceRepository;
import com.impulse.lean.domain.repository.EvidenceValidationRepository;

/**
 * IMPULSE LEAN v1 - Evidence Service Implementation
 * 
 * Business logic implementation for evidence operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Service
@Transactional
public class EvidenceServiceImpl implements EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final EvidenceValidationRepository evidenceValidationRepository;

    @Autowired
    public EvidenceServiceImpl(EvidenceRepository evidenceRepository,
                              EvidenceValidationRepository evidenceValidationRepository) {
        this.evidenceRepository = evidenceRepository;
        this.evidenceValidationRepository = evidenceValidationRepository;
    }

    // ========== BASIC CRUD OPERATIONS ==========

    @Override
    @Transactional(readOnly = true)
    public Optional<Evidence> findById(Long id) {
        return evidenceRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Evidence> findByUuid(String uuid) {
        return evidenceRepository.findByUuid(uuid);
    }

    @Override
    public Evidence save(Evidence evidence) {
        return evidenceRepository.save(evidence);
    }

    @Override
    public void deleteById(Long id) {
        evidenceRepository.deleteById(id);
    }

    // ========== EVIDENCE LIFECYCLE ==========

    @Override
    public Evidence createEvidence(ChallengeParticipation participation, EvidenceCreateRequestDto request) {
        // Validate request
        if (!request.isValidForType()) {
            throw new IllegalArgumentException("Invalid content for evidence type: " + request.getType());
        }
        
        if (!request.hasValidFileSize()) {
            throw new IllegalArgumentException("File size exceeds maximum allowed for type: " + request.getType());
        }
        
        if (!request.hasValidMimeType()) {
            throw new IllegalArgumentException("Invalid MIME type for evidence type: " + request.getType());
        }

        Evidence evidence = new Evidence();
        evidence.setUuid(UUID.randomUUID().toString());
        evidence.setParticipation(participation);
        evidence.setType(request.getType());
        evidence.setContent(request.getContent());
        evidence.setDescription(request.getDescription());
        evidence.setDayNumber(request.getDayNumber());
        evidence.setFilePath(request.getFilePath());
        evidence.setMimeType(request.getMimeType());
        evidence.setFileSize(request.getFileSize());
        evidence.setMetadata(request.getMetadata());
        evidence.setStatus(EvidenceStatus.PENDING);
        
        return evidenceRepository.save(evidence);
    }

    @Override
    public Evidence updateEvidence(String uuid, EvidenceUpdateRequestDto request) {
        Evidence evidence = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + uuid));
        
        // Only allow updates on pending evidence
        if (evidence.getStatus() != EvidenceStatus.PENDING) {
            throw new IllegalStateException("Cannot update evidence that is already validated");
        }
        
        // Update fields if provided
        if (request.getType() != null) {
            evidence.setType(request.getType());
        }
        if (request.hasContent()) {
            evidence.setContent(request.getContent());
        }
        if (request.getDescription() != null) {
            evidence.setDescription(request.getDescription());
        }
        if (request.getDayNumber() != null) {
            evidence.setDayNumber(request.getDayNumber());
        }
        if (request.hasFileUpdate()) {
            evidence.setFilePath(request.getFilePath());
            evidence.setMimeType(request.getMimeType());
            evidence.setFileSize(request.getFileSize());
        }
        if (request.getMetadata() != null) {
            evidence.setMetadata(request.getMetadata());
        }
        
        return evidenceRepository.save(evidence);
    }

    @Override
    public Evidence deleteEvidence(String uuid) {
        Evidence evidence = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + uuid));
        
        // Soft delete by setting status to REJECTED
        evidence.setStatus(EvidenceStatus.REJECTED);
        evidence.setRejectionReason("Deleted by user");
        evidence.setValidatedAt(LocalDateTime.now());
        
        return evidenceRepository.save(evidence);
    }

    // ========== EVIDENCE VALIDATION WORKFLOW ==========

    @Override
    public Evidence approveEvidence(String uuid, User validator, BigDecimal score, String comments) {
        Evidence evidence = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + uuid));
        
        if (!canEvidenceBeValidated(uuid, validator.getUuid())) {
            throw new IllegalStateException("Evidence cannot be validated by this user");
        }
        
        // Create validation record
        EvidenceValidation validation = new EvidenceValidation();
        validation.setEvidence(evidence);
        validation.setValidator(validator);
        validation.setValidationType(ValidationType.PEER);
        validation.setScore(score);
        validation.setFeedback(comments);
        validation.setValidatedAt(LocalDateTime.now());
        
        evidenceValidationRepository.save(validation);
        
        // Update evidence status
        evidence.approve(score);
        
        return evidenceRepository.save(evidence);
    }

    @Override
    public Evidence rejectEvidence(String uuid, User validator, String reason) {
        Evidence evidence = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + uuid));
        
        if (!canEvidenceBeValidated(uuid, validator.getUuid())) {
            throw new IllegalStateException("Evidence cannot be validated by this user");
        }
        
        // Create validation record
        EvidenceValidation validation = new EvidenceValidation();
        validation.setEvidence(evidence);
        validation.setValidator(validator);
        validation.setValidationType(ValidationType.PEER);
        validation.setScore(BigDecimal.ZERO);
        validation.setFeedback(reason);
        validation.setValidatedAt(LocalDateTime.now());
        
        evidenceValidationRepository.save(validation);
        
        // Update evidence status
        evidence.reject(reason);
        
        return evidenceRepository.save(evidence);
    }

    @Override
    public Evidence flagEvidence(String uuid, User validator, String reason) {
        Evidence evidence = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + uuid));
        
        // Create validation record
        EvidenceValidation validation = new EvidenceValidation();
        validation.setEvidence(evidence);
        validation.setValidator(validator);
        validation.setValidationType(ValidationType.MODERATOR);
        validation.setScore(BigDecimal.ZERO);
        validation.setFeedback(reason);
        validation.setValidatedAt(LocalDateTime.now());
        
        evidenceValidationRepository.save(validation);
        
        // Update evidence status
        evidence.flag(reason);
        
        return evidenceRepository.save(evidence);
    }

    @Override
    public Evidence validateEvidence(String uuid, User validator, EvidenceValidationRequestDto request) {
        Evidence evidence = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + uuid));
        
        if (!canEvidenceBeValidated(uuid, validator.getUuid())) {
            throw new IllegalStateException("Evidence cannot be validated by this user");
        }
        
        // Create validation record
        EvidenceValidation validation = new EvidenceValidation();
        validation.setEvidence(evidence);
        validation.setValidator(validator);
        validation.setValidationType(request.getValidationType());
        validation.setScore(request.getScore());
        validation.setFeedback(request.getFeedback());
        validation.setConfidenceLevel(request.getConfidenceLevel());
        validation.setValidatedAt(LocalDateTime.now());
        
        // Set criteria scores as JSON if provided
        if (request.hasQualityCriteriaScores()) {
            String criteriaJson = String.format(
                "{\"creativity\": %s, \"completeness\": %s, \"clarity\": %s, \"relevance\": %s}",
                request.getCreativityScore(),
                request.getCompletenessScore(),
                request.getClarityScore(),
                request.getRelevanceScore()
            );
            validation.setCriteriaScores(criteriaJson);
        }
        
        evidenceValidationRepository.save(validation);
        
        // Update evidence based on validation decision
        if (request.isApprovalDecision()) {
            evidence.approve(request.getScore());
        } else if (request.isRejectionDecision()) {
            evidence.reject(request.getRejectionReason());
        }
        
        return evidenceRepository.save(evidence);
    }

    // ========== EVIDENCE QUERIES ==========

    @Override
    @Transactional(readOnly = true)
    public List<Evidence> findEvidenceByParticipation(ChallengeParticipation participation) {
        return evidenceRepository.findByParticipation(participation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evidence> findEvidenceByUser(User user) {
        return evidenceRepository.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evidence> findEvidenceByChallenge(Long challengeId) {
        return evidenceRepository.findByChallengeId(challengeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evidence> findEvidenceByStatus(EvidenceStatus status) {
        return evidenceRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evidence> findEvidenceByType(EvidenceType type) {
        return evidenceRepository.findByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Evidence> findEvidence(Pageable pageable) {
        return evidenceRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Evidence> searchEvidence(String searchTerm, Pageable pageable) {
        return evidenceRepository.searchEvidence(searchTerm, pageable);
    }

    // ========== EVIDENCE VALIDATION CHECKS ==========

    @Override
    @Transactional(readOnly = true)
    public boolean canEvidenceBeValidated(String uuid, String validatorUuid) {
        Evidence evidence = findByUuid(uuid).orElse(null);
        if (evidence == null || evidence.getStatus() != EvidenceStatus.PENDING) {
            return false;
        }
        
        // Check if validator is the same as evidence submitter
        if (evidence.getParticipation().getUser().getUuid().equals(validatorUuid)) {
            return false;
        }
        
        // Check if validator has already validated this evidence
        return !hasUserValidatedEvidence(uuid, validatorUuid);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEvidenceValidated(String uuid) {
        Evidence evidence = findByUuid(uuid).orElse(null);
        return evidence != null && evidence.isValidated();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserValidatedEvidence(String evidenceUuid, String userUuid) {
        Evidence evidence = findByUuid(evidenceUuid).orElse(null);
        if (evidence == null) return false;
        
        return evidence.getValidations().stream()
            .anyMatch(v -> v.getValidator() != null && 
                          v.getValidator().getUuid().equals(userUuid));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean requiresValidation(String uuid) {
        Evidence evidence = findByUuid(uuid).orElse(null);
        return evidence != null && evidence.requiresValidation();
    }

    // ========== FILE MANAGEMENT ==========

    @Override
    public Evidence attachFile(String uuid, String filePath, String mimeType, Long fileSize) {
        Evidence evidence = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + uuid));
        
        evidence.setFilePath(filePath);
        evidence.setMimeType(mimeType);
        evidence.setFileSize(fileSize);
        
        return evidenceRepository.save(evidence);
    }

    @Override
    public Evidence updateFileMetadata(String uuid, String mimeType, Long fileSize) {
        Evidence evidence = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + uuid));
        
        if (mimeType != null) {
            evidence.setMimeType(mimeType);
        }
        if (fileSize != null) {
            evidence.setFileSize(fileSize);
        }
        
        return evidenceRepository.save(evidence);
    }

    @Override
    @Transactional(readOnly = true)
    public String generateFileUrl(String uuid) {
        Evidence evidence = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + uuid));
        
        if (evidence.getFilePath() == null) {
            return null;
        }
        
        // TODO: Implement file URL generation logic
        // This would typically involve generating a presigned URL for cloud storage
        return "/api/v1/evidence/" + uuid + "/file";
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateFileType(String mimeType, EvidenceType type) {
        if (mimeType == null || type == null) return false;
        
        String[] allowedTypes = type.getAllowedMimeTypes();
        for (String allowedType : allowedTypes) {
            if (mimeType.startsWith(allowedType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateFileSize(Long fileSize, EvidenceType type) {
        if (fileSize == null || type == null) return true;
        return fileSize <= type.getMaxFileSize();
    }

    // ========== ANALYTICS ==========

    @Override
    @Transactional(readOnly = true)
    public List<Evidence> findPendingValidation() {
        return evidenceRepository.findByStatus(EvidenceStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evidence> findRecentEvidence(int limit) {
        return evidenceRepository.findAll(org.springframework.data.domain.PageRequest.of(0, limit, 
            org.springframework.data.domain.Sort.by("submittedAt").descending())).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evidence> findTopRatedEvidence(int limit) {
        return evidenceRepository.findTopScoredEvidence(org.springframework.data.domain.PageRequest.of(0, limit));
    }

    @Override
    @Transactional(readOnly = true)
    public long countEvidenceByStatus(EvidenceStatus status) {
        return evidenceRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countEvidenceByType(EvidenceType type) {
        return evidenceRepository.countByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAverageValidationScore(Long challengeId) {
        List<Evidence> evidenceList = evidenceRepository.findByChallengeId(challengeId);
        
        if (evidenceList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal sum = evidenceList.stream()
            .filter(e -> e.getValidationScore() != null)
            .map(Evidence::getValidationScore)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long count = evidenceList.stream()
            .filter(e -> e.getValidationScore() != null)
            .count();
        
        return count > 0 ? sum.divide(new BigDecimal(count), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    // ========== BATCH OPERATIONS ==========

    @Override
    @Transactional(readOnly = true)
    public List<Evidence> findExpiredPendingEvidence(LocalDateTime threshold) {
        return evidenceRepository.findOldPendingEvidence(threshold);
    }

    @Override
    public void autoRejectExpiredEvidence(LocalDateTime threshold) {
        List<Evidence> expiredEvidence = findExpiredPendingEvidence(threshold);
        
        for (Evidence evidence : expiredEvidence) {
            evidence.reject("Automatically rejected due to timeout");
            evidenceRepository.save(evidence);
        }
    }

    @Override
    public void cleanupOldEvidence(LocalDateTime threshold) {
        // TODO: Implement cleanup logic for old evidence
        // This could involve archiving or physically deleting old evidence
    }

    // ========== CONTENT MODERATION ==========

    @Override
    @Transactional(readOnly = true)
    public List<Evidence> findFlaggedEvidence() {
        return evidenceRepository.findByStatus(EvidenceStatus.FLAGGED);
    }

    @Override
    public Evidence moderateEvidence(String uuid, User moderator, boolean approved, String reason) {
        Evidence evidence = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + uuid));
        
        // Create moderation validation record
        EvidenceValidation validation = new EvidenceValidation();
        validation.setEvidence(evidence);
        validation.setValidator(moderator);
        validation.setValidationType(ValidationType.MODERATOR);
        validation.setScore(approved ? new BigDecimal("0.5") : BigDecimal.ZERO);
        validation.setFeedback(reason);
        validation.setValidatedAt(LocalDateTime.now());
        
        evidenceValidationRepository.save(validation);
        
        // Update evidence status
        if (approved) {
            evidence.setStatus(EvidenceStatus.PENDING); // Return to pending for peer review
        } else {
            evidence.reject("Content moderation: " + reason);
        }
        
        return evidenceRepository.save(evidence);
    }

    @Override
    public void reportInappropriateContent(String uuid, User reporter, String reason) {
        Evidence evidence = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + uuid));
        
        // Create report record
        EvidenceValidation report = new EvidenceValidation();
        report.setEvidence(evidence);
        report.setValidator(reporter);
        report.setValidationType(ValidationType.PEER);
        report.setScore(BigDecimal.ZERO);
        report.setFeedback("Report: " + reason);
        report.setValidatedAt(LocalDateTime.now());
        
        evidenceValidationRepository.save(report);
        
        // Flag evidence for review
        evidence.flag("Reported: " + reason);
        evidenceRepository.save(evidence);
    }

    // ========== RECOMMENDATIONS ==========

    @Override
    @Transactional(readOnly = true)
    public List<Evidence> findSimilarEvidence(String uuid) {
        // TODO: Implement similarity algorithm
        // This could use content analysis, tags, or machine learning
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evidence> findExampleEvidence(Long challengeId, EvidenceType type) {
        return evidenceRepository.findByChallengeId(challengeId)
            .stream()
            .filter(e -> e.getType() == type)
            .filter(e -> e.getStatus() == EvidenceStatus.APPROVED)
            .filter(e -> e.getValidationScore() != null && 
                        e.getValidationScore().compareTo(new BigDecimal("0.7")) >= 0)
            .limit(5)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evidence> getBestPracticeExamples(Long challengeId) {
        return evidenceRepository.findByChallengeIdOrderByScore(challengeId, 
            org.springframework.data.domain.PageRequest.of(0, 10));
    }

    // ========== STATISTICS ==========

    @Override
    @Transactional(readOnly = true)
    public double getEvidenceQualityScore(String uuid) {
        Evidence evidence = findByUuid(uuid).orElse(null);
        if (evidence == null || evidence.getValidationScore() == null) {
            return 0.0;
        }
        return evidence.getValidationScore().doubleValue();
    }

    @Override
    @Transactional(readOnly = true)
    public int getValidationProgress(String uuid) {
        Evidence evidence = findByUuid(uuid).orElse(null);
        if (evidence == null) return 0;
        
        int currentValidations = evidence.getValidationCount();
        int requiredValidations = evidence.getRequiredValidationCount();
        
        return requiredValidations > 0 ? (currentValidations * 100) / requiredValidations : 100;
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDateTime getEstimatedValidationTime(String uuid) {
        // TODO: Implement estimation algorithm based on validation queue and historical data
        return LocalDateTime.now().plusHours(24);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evidence> findEvidenceNeedingValidation(String validatorUuid) {
        return evidenceRepository.findByStatus(EvidenceStatus.PENDING)
            .stream()
            .filter(e -> canEvidenceBeValidated(e.getUuid(), validatorUuid))
            .limit(10)
            .toList();
    }
}
