package com.impulse.application.validator.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidatorResponse {
    private final String id;
    private final String name;
    private final String email;
    private final String specializations;
    private final Boolean isActive;
    private final Double rating;
    private final Integer experienceYears;
}
