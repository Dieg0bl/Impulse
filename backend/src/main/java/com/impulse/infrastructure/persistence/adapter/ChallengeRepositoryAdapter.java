package com.impulse.infrastructure.persistence.adapter;

import com.impulse.domain.challenge.Challenge;
import com.impulse.domain.challenge.ChallengeStatus;
import com.impulse.ports.out.ChallengeRepositoryPort;
import com.impulse.infrastructure.persistence.repository.ChallengeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA adapter implementation of ChallengeRepositoryPort
 */
@Component
@RequiredArgsConstructor
public class ChallengeRepositoryAdapter implements ChallengeRepositoryPort {
    
    private final ChallengeJpaRepository challengeJpaRepository;

    @Override
    public Challenge save(Challenge challenge) {
        return challengeJpaRepository.save(challenge);
    }

    @Override
    public Optional<Challenge> findById(String id) {
        return challengeJpaRepository.findById(id);
    }

    @Override
    public List<Challenge> findByStatus(ChallengeStatus status) {
        return challengeJpaRepository.findByStatus(status);
    }

    @Override
    public List<Challenge> findByCreatorId(String creatorId) {
        return challengeJpaRepository.findByCreatorId(creatorId);
    }

    @Override
    public List<Challenge> findByChallengeType(String challengeType) {
        return challengeJpaRepository.findByChallengeType(challengeType);
    }

    @Override
    public List<Challenge> findActiveAndNotExpired() {
        return challengeJpaRepository.findActiveAndNotExpired(ChallengeStatus.ACTIVE, LocalDateTime.now());
    }

    @Override
    public List<Challenge> findExpiredChallenges() {
        return challengeJpaRepository.findExpiredChallenges(LocalDateTime.now());
    }

    @Override
    public List<Challenge> findRecentChallenges(LocalDateTime since) {
        return challengeJpaRepository.findByStatusAndCreatedAtAfter(ChallengeStatus.ACTIVE, since);
    }

    @Override
    public List<Challenge> searchChallenges(String searchTerm) {
        return challengeJpaRepository.searchChallenges(searchTerm);
    }

    @Override
    public List<Challenge> findAll() {
        return challengeJpaRepository.findAll();
    }

    @Override
    public void deleteById(String id) {
        challengeJpaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return challengeJpaRepository.count();
    }

    @Override
    public long countByCreatorIdAndStatus(String creatorId, ChallengeStatus status) {
        return challengeJpaRepository.countByCreatorIdAndStatus(creatorId, status);
    }
}
