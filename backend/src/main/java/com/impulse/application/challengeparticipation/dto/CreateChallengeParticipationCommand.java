package com.impulse.application.challengeparticipation.dto;

import com.impulse.domain.enums.ParticipationStatus;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class CreateChallengeParticipationCommand {
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
