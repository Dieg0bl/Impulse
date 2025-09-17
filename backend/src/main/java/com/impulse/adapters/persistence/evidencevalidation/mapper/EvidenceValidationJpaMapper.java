package com.impulse.adapters.persistence.evidencevalidation.mapper;

import com.impulse.adapters.persistence.evidencevalidation.entity.EvidenceValidationJpaEntity;
import com.impulse.domain.evidencevalidation.EvidenceValidation;
import com.impulse.domain.evidencevalidation.EvidenceValidationId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EvidenceValidationJpaMapper {

    public EvidenceValidationJpaEntity toEntity(EvidenceValidation domain) {
        if (domain == null) {
            return null;
        }

        EvidenceValidationJpaEntity entity = new EvidenceValidationJpaEntity();
        entity.setId(UUID.fromString(domain.getId().getValue()));
        entity.setEvidenceId(UUID.fromString(domain.getEvidenceId()));
        entity.setValidatorId(UUID.fromString(domain.getValidatorId()));
        entity.setStatus(domain.getStatus());
        entity.setComments(domain.getComments());
        entity.setFeedback(domain.getFeedback());
        entity.setScore(domain.getScore());
        entity.setValidatedAt(domain.getValidationDate());

        return entity;
    }

    public EvidenceValidation toDomain(EvidenceValidationJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return EvidenceValidation.builder()
                .id(new EvidenceValidationId(entity.getId().toString()))
                .evidenceId(entity.getEvidenceId().toString())
                .validatorId(entity.getValidatorId().toString())
                .status(entity.getStatus())
                .comments(entity.getComments())
                .feedback(entity.getFeedback())
                .score(entity.getScore())
                .validationDate(entity.getValidatedAt())
                .build();
    }
}
