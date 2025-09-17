package com.impulse.domain.gamification;

import com.impulse.domain.user.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserStreak {
    private final UUID id;
    private final UserId userId;
    private final int currentStreak;
    private final int longestStreak;
    private final LocalDateTime lastActivityDate;
    private final int freezesUsed;
    private final int freezesAvailable;
    private final String streakType;
    private final boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static UserStreak reconstruct(UUID id, UserId userId, int currentStreak, int longestStreak,
                                       LocalDateTime lastActivityDate, int freezesUsed, int freezesAvailable,
                                       String streakType, boolean isActive, LocalDateTime createdAt,
                                       LocalDateTime updatedAt) {
        return UserStreak.builder()
                .id(id)
                .userId(userId)
                .currentStreak(currentStreak)
                .longestStreak(longestStreak)
                .lastActivityDate(lastActivityDate)
                .freezesUsed(freezesUsed)
                .freezesAvailable(freezesAvailable)
                .streakType(streakType)
                .isActive(isActive)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static UserStreak create(UserId userId, String streakType, int initialFreezes) {
        LocalDateTime now = LocalDateTime.now();
        return UserStreak.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .currentStreak(0)
                .longestStreak(0)
                .lastActivityDate(now)
                .freezesUsed(0)
                .freezesAvailable(initialFreezes)
                .streakType(streakType)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
