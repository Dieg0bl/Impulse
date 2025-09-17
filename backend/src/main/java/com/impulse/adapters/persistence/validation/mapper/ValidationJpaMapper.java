package com.impulse.adapters.persistence.validation.mapper;

import com.impulse.adapters.persistence.validation.entity.ValidationJpaEntity;
import com.impulse.domain.validation.Validation;
import com.impulse.domain.validation.ValidationId;
import com.impulse.domain.enums.ValidationStatus;
import org.springframework.stereotype.Component;

@Component
public class ValidationJpaMapper {
    public ValidationJpaEntity toEntity(Validation validation) {
        ValidationJpaEntity entity = new ValidationJpaEntity();
        if (validation.getId() != null) entity.setId(validation.getId().getValue());
        entity.setEvidenceId(validation.getEvidenceId());
        entity.setValidatorUserId(validation.getValidatorUserId());
        entity.setComments(validation.getComments());
        entity.setStatus(validation.getStatus());
        entity.setCreatedAt(validation.getCreatedAt());
        entity.setUpdatedAt(validation.getUpdatedAt());
        return entity;
    }
    public Validation toDomain(ValidationJpaEntity entity) {
        return Validation.builder()
                .id(entity.getId() != null ? ValidationId.of(entity.getId()) : null)
                .evidenceId(entity.getEvidenceId())
                .validatorUserId(entity.getValidatorUserId())
                .comments(entity.getComments())
                .status(entity.getStatus() != null ? entity.getStatus() : ValidationStatus.PENDING)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}


