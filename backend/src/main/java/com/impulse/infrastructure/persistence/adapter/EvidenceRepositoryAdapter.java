package com.impulse.infrastructure.persistence.adapter;

import com.impulse.domain.evidence.Evidence;
import com.impulse.domain.evidence.EvidenceStatus;
import com.impulse.ports.out.EvidenceRepositoryPort;
import com.impulse.infrastructure.persistence.repository.EvidenceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA adapter implementation of EvidenceRepositoryPort
 */
@Component
@RequiredArgsConstructor
public class EvidenceRepositoryAdapter implements EvidenceRepositoryPort {
    
    private final EvidenceJpaRepository evidenceJpaRepository;

    @Override
    public Evidence save(Evidence evidence) {
        return evidenceJpaRepository.save(evidence);
    }

    @Override
    public Optional<Evidence> findById(String id) {
        return evidenceJpaRepository.findById(id);
    }

    @Override
    public List<Evidence> findByUserId(String userId) {
        return evidenceJpaRepository.findByUserId(userId);
    }

    @Override
    public List<Evidence> findByChallengeId(String challengeId) {
        return evidenceJpaRepository.findByChallengeId(challengeId);
    }

    @Override
    public List<Evidence> findByStatus(EvidenceStatus status) {
        return evidenceJpaRepository.findByStatus(status);
    }

    @Override
    public List<Evidence> findByValidatorId(String validatorId) {
        return evidenceJpaRepository.findByValidatorId(validatorId);
    }

    @Override
    public List<Evidence> findByEvidenceType(String evidenceType) {
        return evidenceJpaRepository.findByEvidenceType(evidenceType);
    }

    @Override
    public List<Evidence> findByUserIdAndStatus(String userId, EvidenceStatus status) {
        return evidenceJpaRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    public List<Evidence> findByChallengeIdAndStatus(String challengeId, EvidenceStatus status) {
        return evidenceJpaRepository.findByChallengeIdAndStatus(challengeId, status);
    }

    @Override
    public List<Evidence> findRecentEvidence(LocalDateTime since) {
        return evidenceJpaRepository.findByStatusAndCreatedAtAfter(EvidenceStatus.PENDING, since);
    }

    @Override
    public List<Evidence> findUnassignedByStatus(EvidenceStatus status) {
        return evidenceJpaRepository.findUnassignedByStatus(status);
    }

    @Override
    public List<Evidence> searchEvidence(String searchTerm) {
        return evidenceJpaRepository.searchEvidence(searchTerm);
    }

    @Override
    public List<Evidence> findAll() {
        return evidenceJpaRepository.findAll();
    }

    @Override
    public void deleteById(String id) {
        evidenceJpaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return evidenceJpaRepository.count();
    }

    @Override
    public long countByChallengeId(String challengeId) {
        return evidenceJpaRepository.countByChallengeId(challengeId);
    }

    @Override
    public long countByUserIdAndStatus(String userId, EvidenceStatus status) {
        return evidenceJpaRepository.countByUserIdAndStatus(userId, status);
    }
}
