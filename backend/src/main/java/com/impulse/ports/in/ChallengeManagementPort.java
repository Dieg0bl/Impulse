package com.impulse.ports.in;

import com.impulse.domain.challenge.Challenge;
import java.util.List;
import java.util.UUID;

/**
 * Input port for challenge management operations
 */
public interface ChallengeManagementPort {
    
    Challenge createChallenge(String title, String description, UUID creatorId);
    
    Challenge findById(UUID id);
    
    List<Challenge> findActivechallenges();
    
    List<Challenge> findByCreator(UUID creatorId);
    
    void publishChallenge(UUID challengeId);
    
    void completeChallenge(UUID challengeId);
}
