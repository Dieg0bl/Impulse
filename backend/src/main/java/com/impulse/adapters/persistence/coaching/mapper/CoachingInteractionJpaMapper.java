package com.impulse.adapters.persistence.coaching.mapper;

import com.impulse.adapters.persistence.coaching.entity.CoachingInteractionJpaEntity;
import com.impulse.domain.coachinginteraction.CoachingInteraction;
import com.impulse.domain.coachinginteraction.CoachingInteractionId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CoachingInteractionJpaMapper {

    public CoachingInteractionJpaEntity toEntity(CoachingInteraction domain) {
        if (domain == null) {
            return null;
        }

        CoachingInteractionJpaEntity entity = new CoachingInteractionJpaEntity();
        entity.setId(UUID.fromString(domain.getId().getValue()));
        entity.setSessionId(domain.getSessionId());
        entity.setType(domain.getType());
        entity.setContent(domain.getContent());
        entity.setTimestamp(domain.getTimestamp());
        entity.setUserId(domain.getUserId());
        entity.setCoachId(domain.getCoachId());
        entity.setInteractionMetadata(domain.getInteractionMetadata());

        return entity;
    }

    public CoachingInteraction toDomain(CoachingInteractionJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return CoachingInteraction.builder()
                .id(new CoachingInteractionId(entity.getId().toString()))
                .sessionId(entity.getSessionId())
                .type(entity.getType())
                .content(entity.getContent())
                .timestamp(entity.getTimestamp())
                .userId(entity.getUserId())
                .coachId(entity.getCoachId())
                .interactionMetadata(entity.getInteractionMetadata())
                .build();
    }
}
