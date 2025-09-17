package com.impulse.application.validation.dto;

import com.impulse.domain.enums.ValidationStatus;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class ValidationResponse {
    private final Long id;
    private final String evidenceId;
    private final String validatorUserId;
    private final String comments;
    private final ValidationStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
