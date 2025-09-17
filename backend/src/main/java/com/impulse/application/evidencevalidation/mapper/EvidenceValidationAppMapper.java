package com.impulse.application.evidencevalidation.mapper;

import com.impulse.application.evidencevalidation.dto.CreateEvidenceValidationCommand;
import com.impulse.application.evidencevalidation.dto.EvidenceValidationResponse;
import com.impulse.domain.evidencevalidation.EvidenceValidation;
import org.springframework.stereotype.Component;

@Component
public class EvidenceValidationAppMapper {
    public EvidenceValidation toDomain(CreateEvidenceValidationCommand command) {
        return EvidenceValidation.builder()
                .id(null)
                .evidenceId(command.getEvidenceId())
                .validatorId(command.getValidatorId())
                .status(command.getStatus())
                .comments(command.getComments())
                .validationDate(command.getValidationDate())
                .score(command.getScore())
                .feedback(command.getFeedback())
                .build();
    }
    public EvidenceValidationResponse toResponse(EvidenceValidation evidenceValidation) {
        return EvidenceValidationResponse.builder()
                .id(evidenceValidation.getId() != null ? evidenceValidation.getId().getValue() : null)
                .evidenceId(evidenceValidation.getEvidenceId())
                .validatorId(evidenceValidation.getValidatorId())
                .status(evidenceValidation.getStatus())
                .comments(evidenceValidation.getComments())
                .validationDate(evidenceValidation.getValidationDate())
                .score(evidenceValidation.getScore())
                .feedback(evidenceValidation.getFeedback())
                .build();
    }
}
