package com.impulse.domain.coachinginteraction;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class CoachingInteraction {
    private final CoachingInteractionId id;
    private final String sessionId;
    private final String type;
    private final String content;
    private final LocalDateTime timestamp;
    private final String userId;
    private final String metadata;
}
