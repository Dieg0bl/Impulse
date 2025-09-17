package com.impulse.application.validator.mapper;

import com.impulse.application.validator.dto.CreateValidatorCommand;
import com.impulse.application.validator.dto.ValidatorResponse;
import com.impulse.domain.validator.Validator;
import org.springframework.stereotype.Component;

@Component
public class ValidatorAppMapper {
    public Validator toDomain(CreateValidatorCommand command) {
        return Validator.builder()
                .id(null)
                .name(command.getName())
                .email(command.getEmail())
                .specializations(command.getSpecializations())
                .isActive(command.getIsActive())
                .rating(command.getRating())
                .experienceYears(command.getExperienceYears())
                .build();
    }
    public ValidatorResponse toResponse(Validator validator) {
        return ValidatorResponse.builder()
                .id(validator.getId() != null ? validator.getId().getValue() : null)
                .name(validator.getName())
                .email(validator.getEmail())
                .specializations(validator.getSpecializations())
                .isActive(validator.getIsActive())
                .rating(validator.getRating())
                .experienceYears(validator.getExperienceYears())
                .build();
    }
}
