package com.impulse.evidence.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.impulse.lean.application.dto.evidence.EvidenceCreateRequestDto;
import com.impulse.lean.application.dto.evidence.EvidenceUpdateRequestDto;
import com.impulse.lean.application.dto.evidence.EvidenceValidationRequestDto;
import com.impulse.lean.domain.model.ChallengeParticipation;
import com.impulse.lean.domain.model.Evidence;
import com.impulse.lean.domain.model.EvidenceStatus;
import com.impulse.lean.domain.model.EvidenceType;
import com.impulse.lean.domain.model.User;

/**
 * IMPULSE LEAN v1 - Evidence Service Interface
 * 
 * Business logic interface for evidence operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public interface EvidenceService {

    // Basic CRUD operations
    Optional<Evidence> findById(Long id);
    Optional<Evidence> findByUuid(String uuid);
    Evidence save(Evidence evidence);
    void deleteById(Long id);

    // Evidence lifecycle
    Evidence createEvidence(ChallengeParticipation participation, EvidenceCreateRequestDto request);
    Evidence updateEvidence(String uuid, EvidenceUpdateRequestDto request);
    Evidence deleteEvidence(String uuid);
    
    // Evidence validation workflow
    Evidence approveEvidence(String uuid, User validator, BigDecimal score, String comments);
    Evidence rejectEvidence(String uuid, User validator, String reason);
    Evidence flagEvidence(String uuid, User validator, String reason);
    Evidence validateEvidence(String uuid, User validator, EvidenceValidationRequestDto request);

    // Evidence queries
    List<Evidence> findEvidenceByParticipation(ChallengeParticipation participation);
    List<Evidence> findEvidenceByUser(User user);
    List<Evidence> findEvidenceByChallenge(Long challengeId);
    List<Evidence> findEvidenceByStatus(EvidenceStatus status);
    List<Evidence> findEvidenceByType(EvidenceType type);
    Page<Evidence> findEvidence(Pageable pageable);
    Page<Evidence> searchEvidence(String searchTerm, Pageable pageable);

    // Evidence validation checks
    boolean canEvidenceBeValidated(String uuid, String validatorUuid);
    boolean isEvidenceValidated(String uuid);
    boolean hasUserValidatedEvidence(String evidenceUuid, String userUuid);
    boolean requiresValidation(String uuid);

    // Evidence file management
    Evidence attachFile(String uuid, String filePath, String mimeType, Long fileSize);
    Evidence updateFileMetadata(String uuid, String mimeType, Long fileSize);
    String generateFileUrl(String uuid);
    boolean validateFileType(String mimeType, EvidenceType type);
    boolean validateFileSize(Long fileSize, EvidenceType type);

    // Evidence analytics
    List<Evidence> findPendingValidation();
    List<Evidence> findRecentEvidence(int limit);
    List<Evidence> findTopRatedEvidence(int limit);
    long countEvidenceByStatus(EvidenceStatus status);
    long countEvidenceByType(EvidenceType type);
    BigDecimal getAverageValidationScore(Long challengeId);

    // Batch operations
    List<Evidence> findExpiredPendingEvidence(LocalDateTime threshold);
    void autoRejectExpiredEvidence(LocalDateTime threshold);
    void cleanupOldEvidence(LocalDateTime threshold);

    // Content moderation
    List<Evidence> findFlaggedEvidence();
    Evidence moderateEvidence(String uuid, User moderator, boolean approved, String reason);
    void reportInappropriateContent(String uuid, User reporter, String reason);

    // Evidence recommendations
    List<Evidence> findSimilarEvidence(String uuid);
    List<Evidence> findExampleEvidence(Long challengeId, EvidenceType type);
    List<Evidence> getBestPracticeExamples(Long challengeId);

    // Evidence statistics
    double getEvidenceQualityScore(String uuid);
    int getValidationProgress(String uuid);
    LocalDateTime getEstimatedValidationTime(String uuid);
    List<Evidence> findEvidenceNeedingValidation(String validatorUuid);
}
