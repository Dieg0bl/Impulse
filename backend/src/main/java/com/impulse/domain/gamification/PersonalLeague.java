package com.impulse.domain.gamification;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PersonalLeague {
    private final UUID id;
    private final String name;
    private final String tier;
    private final double minValidationRate;
    private final int minCredPoints;
    private final int minStreakDays;
    private final int windowDays;
    private final String badgeIcon;
    private final String frameColor;
    private final double credBonus;
    private final int prestigePoints;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static PersonalLeague reconstruct(UUID id, String name, String tier, double minValidationRate,
                                           int minCredPoints, int minStreakDays, int windowDays, String badgeIcon,
                                           String frameColor, double credBonus, int prestigePoints, String description,
                                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        return PersonalLeague.builder()
                .id(id)
                .name(name)
                .tier(tier)
                .minValidationRate(minValidationRate)
                .minCredPoints(minCredPoints)
                .minStreakDays(minStreakDays)
                .windowDays(windowDays)
                .badgeIcon(badgeIcon)
                .frameColor(frameColor)
                .credBonus(credBonus)
                .prestigePoints(prestigePoints)
                .description(description)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static PersonalLeague create(String name, String tier, double minValidationRate, int minCredPoints,
                                      int minStreakDays, int windowDays, String badgeIcon, String frameColor,
                                      double credBonus, int prestigePoints, String description) {
        LocalDateTime now = LocalDateTime.now();
        return PersonalLeague.builder()
                .id(UUID.randomUUID())
                .name(name)
                .tier(tier)
                .minValidationRate(minValidationRate)
                .minCredPoints(minCredPoints)
                .minStreakDays(minStreakDays)
                .windowDays(windowDays)
                .badgeIcon(badgeIcon)
                .frameColor(frameColor)
                .credBonus(credBonus)
                .prestigePoints(prestigePoints)
                .description(description)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
