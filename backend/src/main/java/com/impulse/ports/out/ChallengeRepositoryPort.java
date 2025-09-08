package com.impulse.ports.out;

import com.impulse.domain.challenge.Challenge;
import com.impulse.domain.challenge.ChallengeStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Output port for challenge persistence operations
 */
public interface ChallengeRepositoryPort {
    
    Challenge save(Challenge challenge);
    
    Optional<Challenge> findById(String id);
    
    List<Challenge> findByStatus(ChallengeStatus status);
    
    List<Challenge> findByCreatorId(String creatorId);
    
    List<Challenge> findByChallengeType(String challengeType);
    
    List<Challenge> findActiveAndNotExpired();
    
    List<Challenge> findExpiredChallenges();
    
    List<Challenge> findRecentChallenges(LocalDateTime since);
    
    List<Challenge> searchChallenges(String searchTerm);
    
    List<Challenge> findAll();
    
    void deleteById(String id);
    
    long count();
    
    long countByCreatorIdAndStatus(String creatorId, ChallengeStatus status);
}
