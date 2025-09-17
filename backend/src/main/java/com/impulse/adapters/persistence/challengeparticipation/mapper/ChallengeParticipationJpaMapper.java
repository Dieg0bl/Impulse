package com.impulse.adapters.persistence.challengeparticipation.mapper;

import com.impulse.adapters.persistence.challengeparticipation.entity.ChallengeParticipationJpaEntity;
import com.impulse.domain.challengeparticipation.ChallengeParticipation;
import com.impulse.domain.challengeparticipation.ChallengeParticipationId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ChallengeParticipationJpaMapper {

    public ChallengeParticipationJpaEntity toEntity(ChallengeParticipation domain) {
        if (domain == null) {
            return null;
        }

        ChallengeParticipationJpaEntity entity = new ChallengeParticipationJpaEntity();
        entity.setId(UUID.fromString(domain.getId().getValue()));
        entity.setUserId(domain.getUserId());
        entity.setChallengeId(domain.getChallengeId());
        entity.setStatus(domain.getStatus());
        entity.setJoinedAt(domain.getJoinedAt());
        entity.setCompletedAt(domain.getCompletedAt());
        entity.setWithdrawnAt(domain.getWithdrawnAt());
        entity.setWithdrawalReason(domain.getWithdrawalReason());
        entity.setScore(domain.getScore());
        entity.setProgressPercentage(domain.getProgressPercentage());

        return entity;
    }

    public ChallengeParticipation toDomain(ChallengeParticipationJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return ChallengeParticipation.builder()
                .id(new ChallengeParticipationId(entity.getId().toString()))
                .userId(entity.getUserId())
                .challengeId(entity.getChallengeId())
                .status(entity.getStatus())
                .joinedAt(entity.getJoinedAt())
                .completedAt(entity.getCompletedAt())
                .withdrawnAt(entity.getWithdrawnAt())
                .withdrawalReason(entity.getWithdrawalReason())
                .score(entity.getScore())
                .progressPercentage(entity.getProgressPercentage())
                .build();
    }
}
