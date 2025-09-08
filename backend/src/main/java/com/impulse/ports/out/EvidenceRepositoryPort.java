package com.impulse.ports.out;

import com.impulse.domain.evidence.Evidence;
import com.impulse.domain.evidence.EvidenceStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Output port for evidence persistence operations
 */
public interface EvidenceRepositoryPort {
    
    Evidence save(Evidence evidence);
    
    Optional<Evidence> findById(String id);
    
    List<Evidence> findByUserId(String userId);
    
    List<Evidence> findByChallengeId(String challengeId);
    
    List<Evidence> findByStatus(EvidenceStatus status);
    
    List<Evidence> findByValidatorId(String validatorId);
    
    List<Evidence> findByEvidenceType(String evidenceType);
    
    List<Evidence> findByUserIdAndStatus(String userId, EvidenceStatus status);
    
    List<Evidence> findByChallengeIdAndStatus(String challengeId, EvidenceStatus status);
    
    List<Evidence> findRecentEvidence(LocalDateTime since);
    
    List<Evidence> findUnassignedByStatus(EvidenceStatus status);
    
    List<Evidence> searchEvidence(String searchTerm);
    
    List<Evidence> findAll();
    
    void deleteById(String id);
    
    long count();
    
    long countByChallengeId(String challengeId);
    
    long countByUserIdAndStatus(String userId, EvidenceStatus status);
}
