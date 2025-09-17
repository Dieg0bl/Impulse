package com.impulse.application.coachinginteraction.port;

import com.impulse.domain.coachinginteraction.CoachingInteraction;
import com.impulse.domain.coachinginteraction.CoachingInteractionId;
import java.util.List;
import java.util.Optional;

public interface CoachingInteractionRepository {
    CoachingInteraction save(CoachingInteraction coachingInteraction);
    Optional<CoachingInteraction> findById(CoachingInteractionId id);
    List<CoachingInteraction> findBySessionId(String sessionId);
    List<CoachingInteraction> findByUserId(String userId);
}
