package com.impulse.domain.validation;

import com.impulse.domain.enums.ValidationStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Validation {
    private final ValidationId id;
    private final String evidenceId;
    private final String validatorUserId;
    private final String comments;
    private final ValidationStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
