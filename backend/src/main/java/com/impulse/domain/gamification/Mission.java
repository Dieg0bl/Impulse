package com.impulse.domain.gamification;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Mission {
    private final UUID id;
    private final String type;
    private final String category;
    private final String name;
    private final String description;
    private final boolean isActive;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final String difficulty;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static Mission reconstruct(UUID id, String type, String category, String name,
                                    String description, boolean isActive, LocalDateTime startDate,
                                    LocalDateTime endDate, String difficulty, LocalDateTime createdAt,
                                    LocalDateTime updatedAt) {
        return Mission.builder()
                .id(id)
                .type(type)
                .category(category)
                .name(name)
                .description(description)
                .isActive(isActive)
                .startDate(startDate)
                .endDate(endDate)
                .difficulty(difficulty)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static Mission create(String type, String category, String name, String description,
                               boolean isActive, LocalDateTime startDate, LocalDateTime endDate,
                               String difficulty) {
        LocalDateTime now = LocalDateTime.now();
        return Mission.builder()
                .id(UUID.randomUUID())
                .type(type)
                .category(category)
                .name(name)
                .description(description)
                .isActive(isActive)
                .startDate(startDate)
                .endDate(endDate)
                .difficulty(difficulty)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
