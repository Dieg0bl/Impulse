package com.impulse.adapters.persistence.challenge.mapper;

import com.impulse.adapters.persistence.challenge.entity.ChallengeJpaEntity;
import com.impulse.domain.challenge.Challenge;
import com.impulse.domain.challenge.ChallengeId;
import com.impulse.domain.user.UserId;
import org.springframework.stereotype.Component;

/**
 * ChallengeJpaMapper - Mapper entre entidad JPA y entidad de dominio
 */
@Component
public class ChallengeJpaMapper {

    /**
     * Convierte entidad de dominio a entidad JPA
     */
    public ChallengeJpaEntity toJpaEntity(Challenge challenge) {
        if (challenge == null) {
            return null;
        }

        ChallengeJpaEntity jpaEntity = new ChallengeJpaEntity();
        jpaEntity.setId(challenge.getId() != null ? challenge.getId().asLong() : null);
        jpaEntity.setTitle(challenge.getTitle());
        jpaEntity.setDescription(challenge.getDescription());
        jpaEntity.setType(challenge.getType());
        jpaEntity.setDifficulty(challenge.getDifficulty());
        jpaEntity.setStatus(challenge.getStatus());
        jpaEntity.setPointsReward(challenge.getPointsReward());
        jpaEntity.setMonetaryReward(challenge.getMonetaryReward());
        jpaEntity.setStartDate(challenge.getStartDate());
        jpaEntity.setEndDate(challenge.getEndDate());
        jpaEntity.setMaxParticipants(challenge.getMaxParticipants());
        jpaEntity.setCurrentParticipants(challenge.getCurrentParticipants());
        jpaEntity.setRequiresEvidence(challenge.getRequiresEvidence());
        jpaEntity.setAutoValidation(challenge.getAutoValidation());
        jpaEntity.setCreatedBy(challenge.getCreatedBy() != null ? challenge.getCreatedBy().asLong() : null);
        jpaEntity.setCreatedAt(challenge.getCreatedAt());
        jpaEntity.setUpdatedAt(challenge.getUpdatedAt());

        return jpaEntity;
    }

    /**
     * Convierte entidad JPA a entidad de dominio
     */
    public Challenge toDomainEntity(ChallengeJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return Challenge.builder()
                .id(jpaEntity.getId() != null ? ChallengeId.of(jpaEntity.getId()) : null)
                .title(jpaEntity.getTitle())
                .description(jpaEntity.getDescription())
                .type(jpaEntity.getType())
                .difficulty(jpaEntity.getDifficulty())
                .status(jpaEntity.getStatus())
                .pointsReward(jpaEntity.getPointsReward())
                .monetaryReward(jpaEntity.getMonetaryReward())
                .startDate(jpaEntity.getStartDate())
                .endDate(jpaEntity.getEndDate())
                .maxParticipants(jpaEntity.getMaxParticipants())
                .currentParticipants(jpaEntity.getCurrentParticipants())
                .requiresEvidence(jpaEntity.getRequiresEvidence())
                .autoValidation(jpaEntity.getAutoValidation())
                .createdBy(jpaEntity.getCreatedBy() != null ? UserId.of(jpaEntity.getCreatedBy()) : null)
                .createdAt(jpaEntity.getCreatedAt())
                .updatedAt(jpaEntity.getUpdatedAt())
                .build();
    }
}

