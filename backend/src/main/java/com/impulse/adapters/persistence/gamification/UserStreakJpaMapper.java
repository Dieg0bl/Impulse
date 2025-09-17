package com.impulse.adapters.persistence.gamification;

import com.impulse.domain.gamification.UserStreak;
import com.impulse.domain.user.UserId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserStreakJpaMapper {

    public UserStreakJpaEntity toJpaEntity(UserStreak userStreak) {
        if (userStreak == null) return null;

        return new UserStreakJpaEntity(
                userStreak.getId().toString(),
                userStreak.getUserId().getValue().toString(),
                userStreak.getCurrentStreak(),
                userStreak.getLongestStreak(),
                userStreak.getLastActivityDate(),
                userStreak.getFreezesUsed(),
                userStreak.getFreezesAvailable(),
                userStreak.getStreakType(),
                userStreak.isActive(),
                userStreak.getCreatedAt(),
                userStreak.getUpdatedAt()
        );
    }

    public UserStreak toDomainEntity(UserStreakJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;

        return UserStreak.reconstruct(
                UUID.fromString(jpaEntity.getId()),
                new UserId(UUID.fromString(jpaEntity.getUserId())),
                jpaEntity.getCurrentStreak(),
                jpaEntity.getLongestStreak(),
                jpaEntity.getLastActivityDate(),
                jpaEntity.getFreezesUsed(),
                jpaEntity.getFreezesAvailable(),
                jpaEntity.getStreakType(),
                jpaEntity.isActive(),
                jpaEntity.getCreatedAt(),
                jpaEntity.getUpdatedAt()
        );
    }
}
