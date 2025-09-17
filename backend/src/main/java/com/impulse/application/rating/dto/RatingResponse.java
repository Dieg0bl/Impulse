package com.impulse.application.rating.dto;

import com.impulse.domain.enums.RatingType;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class RatingResponse {
    private final Long id;
    private final Integer score;
    private final String comment;
    private final RatingType type;
    private final Boolean isAnonymous;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String raterUserId;
    private final Long challengeId;
    private final Long evidenceId;
    private final Long coachId;
}
