package com.impulse.application.evidencevalidation.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class CreateEvidenceValidationCommand {
    private final String evidenceId;
    private final String validatorId;
    private final String status;
    private final String comments;
    private final LocalDateTime validationDate;
    private final Double score;
    private final String feedback;
}
