package com.impulse.application.validatorassignment.usecase;

import com.impulse.application.validatorassignment.dto.ValidatorAssignmentResponse;
import com.impulse.application.validatorassignment.mapper.ValidatorAssignmentAppMapper;
import com.impulse.application.validatorassignment.port.ValidatorAssignmentRepository;
import com.impulse.domain.validatorassignment.ValidatorAssignment;
import com.impulse.domain.validatorassignment.ValidatorAssignmentId;
import com.impulse.domain.validatorassignment.ValidatorAssignmentDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetValidatorAssignmentByIdUseCase {
    private final ValidatorAssignmentRepository validatorAssignmentRepository;
    private final ValidatorAssignmentAppMapper validatorAssignmentMapper;
    public GetValidatorAssignmentByIdUseCase(ValidatorAssignmentRepository validatorAssignmentRepository, ValidatorAssignmentAppMapper validatorAssignmentMapper) {
        this.validatorAssignmentRepository = validatorAssignmentRepository;
        this.validatorAssignmentMapper = validatorAssignmentMapper;
    }
    public ValidatorAssignmentResponse execute(String validatorAssignmentId) {
        if (validatorAssignmentId == null || validatorAssignmentId.trim().isEmpty()) throw new ValidatorAssignmentDomainError("ValidatorAssignment ID cannot be null or empty");
        ValidatorAssignmentId id = ValidatorAssignmentId.of(validatorAssignmentId);
        ValidatorAssignment validatorAssignment = validatorAssignmentRepository.findById(id)
            .orElseThrow(() -> new ValidatorAssignmentDomainError("ValidatorAssignment not found with ID: " + validatorAssignmentId));
        return validatorAssignmentMapper.toResponse(validatorAssignment);
    }
}
