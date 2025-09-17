package com.impulse.adapters.persistence.coaching.mapper;

import com.impulse.adapters.persistence.coaching.entity.CoachingSessionJpaEntity;
import com.impulse.domain.coachingsession.CoachingSession;
import com.impulse.domain.coachingsession.CoachingSessionId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CoachingSessionJpaMapper {

    public CoachingSessionJpaEntity toEntity(CoachingSession domain) {
        if (domain == null) {
            return null;
        }

        CoachingSessionJpaEntity entity = new CoachingSessionJpaEntity();
        entity.setId(UUID.fromString(domain.getId().getValue()));
        entity.setUserId(domain.getUserId());
        entity.setCoachId(domain.getCoachId());
        entity.setScheduledTime(domain.getScheduledTime());
        entity.setStartTime(domain.getStartTime());
        entity.setEndTime(domain.getEndTime());
        entity.setStatus(domain.getStatus());
        entity.setObjectives(domain.getObjectives());
        entity.setNotes(domain.getNotes());
        entity.setUserFeedback(domain.getUserFeedback());
        entity.setCoachFeedback(domain.getCoachFeedback());

        return entity;
    }

    public CoachingSession toDomain(CoachingSessionJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return CoachingSession.builder()
                .id(new CoachingSessionId(entity.getId().toString()))
                .userId(entity.getUserId())
                .coachId(entity.getCoachId())
                .scheduledTime(entity.getScheduledTime())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(entity.getStatus())
                .objectives(entity.getObjectives())
                .notes(entity.getNotes())
                .userFeedback(entity.getUserFeedback())
                .coachFeedback(entity.getCoachFeedback())
                .build();
    }
}
