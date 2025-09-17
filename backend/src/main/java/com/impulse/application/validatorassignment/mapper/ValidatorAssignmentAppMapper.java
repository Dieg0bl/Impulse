package com.impulse.application.validatorassignment.mapper;

import com.impulse.application.validatorassignment.dto.CreateValidatorAssignmentCommand;
import com.impulse.application.validatorassignment.dto.ValidatorAssignmentResponse;
import com.impulse.domain.validatorassignment.ValidatorAssignment;
import org.springframework.stereotype.Component;

@Component
public class ValidatorAssignmentAppMapper {
    public ValidatorAssignment toDomain(CreateValidatorAssignmentCommand command) {
        return ValidatorAssignment.builder()
                .id(null)
                .validatorId(command.getValidatorId())
                .evidenceId(command.getEvidenceId())
                .assignedDate(command.getAssignedDate())
                .status(command.getStatus())
                .comments(command.getComments())
                .completedDate(command.getCompletedDate())
                .build();
    }
    public ValidatorAssignmentResponse toResponse(ValidatorAssignment validatorAssignment) {
        return ValidatorAssignmentResponse.builder()
                .id(validatorAssignment.getId() != null ? validatorAssignment.getId().getValue() : null)
                .validatorId(validatorAssignment.getValidatorId())
                .evidenceId(validatorAssignment.getEvidenceId())
                .assignedDate(validatorAssignment.getAssignedDate())
                .status(validatorAssignment.getStatus())
                .comments(validatorAssignment.getComments())
                .completedDate(validatorAssignment.getCompletedDate())
                .build();
    }
}
