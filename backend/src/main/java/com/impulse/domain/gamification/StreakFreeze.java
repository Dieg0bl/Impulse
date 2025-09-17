package com.impulse.domain.gamification;

import com.impulse.domain.user.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class StreakFreeze {
    private final UUID id;
    private final UserId userId;
    private final LocalDateTime freezeDate;
    private final String reason;
    private final int duration;
    private final boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static StreakFreeze create(UserId userId, String reason, int duration) {
        LocalDateTime now = LocalDateTime.now();
        return StreakFreeze.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .freezeDate(now)
                .reason(reason)
                .duration(duration)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
