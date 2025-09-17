package com.impulse.domain.coachingsession;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class CoachingSession {
    private final CoachingSessionId id;
    private final String studentId;
    private final String coachId;
    private final LocalDateTime scheduledDate;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String status;
    private final String notes;
    private final String feedback;
    private final Double rating;
    private final Boolean completed;
}
