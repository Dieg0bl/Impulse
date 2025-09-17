package com.impulse.adapters.persistence.validatorassignment.mapper;

import com.impulse.adapters.persistence.validatorassignment.entity.ValidatorAssignmentJpaEntity;
import com.impulse.domain.validatorassignment.ValidatorAssignment;
import com.impulse.domain.validatorassignment.ValidatorAssignmentId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ValidatorAssignmentJpaMapper {

    public ValidatorAssignmentJpaEntity toEntity(ValidatorAssignment domain) {
        if (domain == null) {
            return null;
        }

        ValidatorAssignmentJpaEntity entity = new ValidatorAssignmentJpaEntity();
        entity.setId(domain.getId() != null ? UUID.fromString(domain.getId().getValue()) : null);
        entity.setValidatorId(UUID.fromString(domain.getValidatorId()));
        entity.setEvidenceId(UUID.fromString(domain.getEvidenceId()));
        entity.setAssignedDate(domain.getAssignedDate());
        entity.setStatus(domain.getStatus());
        entity.setComments(domain.getComments());
        entity.setCompletedDate(domain.getCompletedDate());

        return entity;
    }

    public ValidatorAssignment toDomain(ValidatorAssignmentJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return ValidatorAssignment.builder()
                .id(entity.getId() != null ? new ValidatorAssignmentId(entity.getId().toString()) : null)
                .validatorId(entity.getValidatorId().toString())
                .evidenceId(entity.getEvidenceId().toString())
                .assignedDate(entity.getAssignedDate())
                .status(entity.getStatus())
                .comments(entity.getComments())
                .completedDate(entity.getCompletedDate())
                .build();
    }
}
