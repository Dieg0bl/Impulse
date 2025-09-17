package com.impulse.application.validation.mapper;

import com.impulse.application.validation.dto.CreateValidationCommand;
import com.impulse.application.validation.dto.ValidationResponse;
import com.impulse.domain.validation.Validation;
import com.impulse.domain.validation.ValidationId;
import org.springframework.stereotype.Component;

@Component
public class ValidationAppMapper {
    public Validation toDomain(CreateValidationCommand command) {
        return Validation.builder()
                .id(null)
                .evidenceId(command.getEvidenceId())
                .validatorUserId(command.getValidatorUserId())
                .comments(command.getComments())
                .status(command.getStatus())
                .createdAt(command.getCreatedAt())
                .updatedAt(command.getUpdatedAt())
                .build();
    }
    public ValidationResponse toResponse(Validation validation) {
        return ValidationResponse.builder()
                .id(validation.getId() != null ? validation.getId().getValue() : null)
                .evidenceId(validation.getEvidenceId())
                .validatorUserId(validation.getValidatorUserId())
                .comments(validation.getComments())
                .status(validation.getStatus())
                .createdAt(validation.getCreatedAt())
                .updatedAt(validation.getUpdatedAt())
                .build();
    }
}
