package com.impulse.application.validatorassignment.port;

import com.impulse.domain.validatorassignment.ValidatorAssignment;
import com.impulse.domain.validatorassignment.ValidatorAssignmentId;
import java.util.List;
import java.util.Optional;

public interface ValidatorAssignmentRepository {
    ValidatorAssignment save(ValidatorAssignment validatorAssignment);
    Optional<ValidatorAssignment> findById(ValidatorAssignmentId id);
    List<ValidatorAssignment> findByValidatorId(String validatorId);
    List<ValidatorAssignment> findByEvidenceId(String evidenceId);
    List<ValidatorAssignment> findByStatus(String status);
}
