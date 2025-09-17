package com.impulse.domain.gamification;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class MissionReward {
    private final UUID id;
    private final UUID missionId;
    private final String type;
    private final String currency;
    private final Double amount;
    private final String chestId;
    private final String itemId;
    private final String badgeId;
    private final String name;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static MissionReward create(UUID missionId, String type, String currency, Double amount, String name) {
        LocalDateTime now = LocalDateTime.now();
        return MissionReward.builder()
                .id(UUID.randomUUID())
                .missionId(missionId)
                .type(type)
                .currency(currency)
                .amount(amount)
                .name(name)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
