package com.impulse.application.challengeparticipation.mapper;

import com.impulse.application.challengeparticipation.dto.CreateChallengeParticipationCommand;
import com.impulse.application.challengeparticipation.dto.ChallengeParticipationResponse;
import com.impulse.domain.challengeparticipation.ChallengeParticipation;
import com.impulse.domain.challengeparticipation.ChallengeParticipationId;
import org.springframework.stereotype.Component;

@Component
public class ChallengeParticipationAppMapper {
    public ChallengeParticipation toDomain(CreateChallengeParticipationCommand command) {
        return ChallengeParticipation.builder()
                .id(null)
                .userId(command.getUserId())
                .challengeId(command.getChallengeId())
                .status(command.getStatus())
                .joinedAt(command.getJoinedAt())
                .completedAt(command.getCompletedAt())
                .withdrawnAt(command.getWithdrawnAt())
                .withdrawalReason(command.getWithdrawalReason())
                .score(command.getScore())
                .progressPercentage(command.getProgressPercentage())
                .createdAt(command.getCreatedAt())
                .updatedAt(command.getUpdatedAt())
                .build();
    }
    public ChallengeParticipationResponse toResponse(ChallengeParticipation participation) {
        return ChallengeParticipationResponse.builder()
                .id(participation.getId() != null ? participation.getId().getValue() : null)
                .userId(participation.getUserId())
                .challengeId(participation.getChallengeId())
                .status(participation.getStatus())
                .joinedAt(participation.getJoinedAt())
                .completedAt(participation.getCompletedAt())
                .withdrawnAt(participation.getWithdrawnAt())
                .withdrawalReason(participation.getWithdrawalReason())
                .score(participation.getScore())
                .progressPercentage(participation.getProgressPercentage())
                .createdAt(participation.getCreatedAt())
                .updatedAt(participation.getUpdatedAt())
                .build();
    }
}
