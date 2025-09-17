package com.impulse.application.coachprofile.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateCoachProfileCommand {
    private final String name;
    private final String photo;
    private final String specializations;
    private final String languages;
    private final String timezone;
    private final Integer yearsExperience;
    private final Integer certificationsCount;
    private final Integer totalStudents;
    private final Double averageRating;
    private final Double successRate;
    private final Boolean isActive;
}
