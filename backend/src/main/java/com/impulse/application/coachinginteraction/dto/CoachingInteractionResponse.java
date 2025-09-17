package com.impulse.application.coachinginteraction.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class CoachingInteractionResponse {
    private final String id;
    private final String sessionId;
    private final String type;
    private final String content;
    private final LocalDateTime timestamp;
    private final String userId;
    private final String metadata;
}
