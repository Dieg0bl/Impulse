package com.impulse.application.personalplan.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class CreatePersonalPlanCommand {
    private final String userId;
    private final String coachId;
    private final String planDetails;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Boolean isActive;
}
