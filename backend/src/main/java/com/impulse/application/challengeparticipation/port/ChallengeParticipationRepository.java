package com.impulse.application.challengeparticipation.port;

import com.impulse.domain.challengeparticipation.ChallengeParticipation;
import com.impulse.domain.challengeparticipation.ChallengeParticipationId;
import com.impulse.domain.enums.ParticipationStatus;
import java.util.Optional;

public interface ChallengeParticipationRepository {
    ChallengeParticipation save(ChallengeParticipation participation);
    Optional<ChallengeParticipation> findById(ChallengeParticipationId id);
    long countByStatus(ParticipationStatus status);
}
