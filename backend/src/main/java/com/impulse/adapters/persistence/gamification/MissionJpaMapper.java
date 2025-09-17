package com.impulse.adapters.persistence.gamification;

import com.impulse.domain.gamification.Mission;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MissionJpaMapper {

    public MissionJpaEntity toJpaEntity(Mission mission) {
        if (mission == null) return null;

        return new MissionJpaEntity(
                mission.getId().toString(),
                mission.getType(),
                mission.getCategory(),
                mission.getName(),
                mission.getDescription(),
                mission.isActive(),
                mission.getStartDate(),
                mission.getEndDate(),
                mission.getDifficulty(),
                mission.getCreatedAt(),
                mission.getUpdatedAt()
        );
    }

    public Mission toDomainEntity(MissionJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;

        return Mission.reconstruct(
                UUID.fromString(jpaEntity.getId()),
                jpaEntity.getType(),
                jpaEntity.getCategory(),
                jpaEntity.getName(),
                jpaEntity.getDescription(),
                jpaEntity.isActive(),
                jpaEntity.getStartDate(),
                jpaEntity.getEndDate(),
                jpaEntity.getDifficulty(),
                jpaEntity.getCreatedAt(),
                jpaEntity.getUpdatedAt()
        );
    }
}
