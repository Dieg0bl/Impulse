package com.impulse.domain.gamification;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ThematicEvent {
    private final UUID id;
    private final String name;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String theme;
    private final String rewardType;
    private final String rewardValue;
    private final boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ThematicEvent create(String name, String description, LocalDate startDate, LocalDate endDate,
                                     String theme, String rewardType, String rewardValue) {
        LocalDateTime now = LocalDateTime.now();
        return ThematicEvent.builder()
                .id(UUID.randomUUID())
                .name(name)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .theme(theme)
                .rewardType(rewardType)
                .rewardValue(rewardValue)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
}
