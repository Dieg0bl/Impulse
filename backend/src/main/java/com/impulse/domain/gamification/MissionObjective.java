package com.impulse.domain.gamification;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class MissionObjective {
    private final UUID id;
    private final UUID missionId;
    private final String action;
    private final int targetCount;
    private final int currentCount;
    private final boolean isCompleted;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static MissionObjective create(UUID missionId, String action, int targetCount, String description) {
        LocalDateTime now = LocalDateTime.now();
        return MissionObjective.builder()
                .id(UUID.randomUUID())
                .missionId(missionId)
                .action(action)
                .targetCount(targetCount)
                .currentCount(0)
                .isCompleted(false)
                .description(description)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
