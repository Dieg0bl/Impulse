package com.impulse.application.validatorassignment.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class CreateValidatorAssignmentCommand {
    private final String validatorId;
    private final String evidenceId;
    private final LocalDateTime assignedDate;
    private final String status;
    private final String comments;
    private final LocalDateTime completedDate;
}
