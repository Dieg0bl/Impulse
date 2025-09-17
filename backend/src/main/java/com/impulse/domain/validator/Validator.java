package com.impulse.domain.validator;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Validator {
    private final ValidatorId id;
    private final String name;
    private final String email;
    private final String specializations;
    private final Boolean isActive;
    private final Double rating;
    private final Integer experienceYears;
}
