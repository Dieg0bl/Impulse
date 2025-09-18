package com.impulse.features.evidencereview.application.port.out;

import com.impulse.features.evidencereview.domain.Evidence;
import com.impulse.features.evidencereview.domain.EvidenceId;
import com.impulse.shared.enums.EvidenceStatus;
import com.impulse.shared.enums.EvidenceType;
import java.util.List;
import java.util.Optional;

/**
 * Output Port: EvidenceRepository
 * Defines contract for evidence persistence
 */
public interface EvidenceRepository {

    Evidence save(Evidence evidence);

    Optional<Evidence> findById(EvidenceId id);

    List<Evidence> findByChallengeId(Long challengeId);

    List<Evidence> findByParticipantUserId(Long participantUserId);

    List<Evidence> findByStatus(EvidenceStatus status);

    List<Evidence> findByType(EvidenceType type);

    List<Evidence> findPendingEvidence();

    List<Evidence> findByReviewerUserId(Long reviewerUserId);

    void delete(EvidenceId id);

    boolean existsById(EvidenceId id);

    long countByChallengeId(Long challengeId);

    long countByStatus(EvidenceStatus status);

    long countByParticipantUserId(Long participantUserId);
}
