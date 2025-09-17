package com.impulse.domain.personalplan;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PersonalPlan {
    private final PersonalPlanId id;
    private final String userId;
    private final String coachId;
    private final String planDetails;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Boolean isActive;
}
