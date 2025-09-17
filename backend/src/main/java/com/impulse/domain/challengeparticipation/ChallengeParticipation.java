package com.impulse.domain.challengeparticipation;

import com.impulse.domain.enums.ParticipationStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChallengeParticipation {
    private final ChallengeParticipationId id;
    private final String userId;
    private final Long challengeId;
    private final ParticipationStatus status;
    private final LocalDateTime joinedAt;
    private final LocalDateTime completedAt;
    private final LocalDateTime withdrawnAt;
    private final String withdrawalReason;
    private final Integer score;
    private final Integer progressPercentage;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
