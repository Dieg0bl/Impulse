package com.impulse.application.validatorassignment.usecase;

import com.impulse.application.validatorassignment.dto.CreateValidatorAssignmentCommand;
import com.impulse.application.validatorassignment.dto.ValidatorAssignmentResponse;
import com.impulse.application.validatorassignment.mapper.ValidatorAssignmentAppMapper;
import com.impulse.application.validatorassignment.port.ValidatorAssignmentRepository;
import com.impulse.domain.validatorassignment.ValidatorAssignment;
import com.impulse.domain.validatorassignment.ValidatorAssignmentDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateValidatorAssignmentUseCase {
    private final ValidatorAssignmentRepository validatorAssignmentRepository;
    private final ValidatorAssignmentAppMapper validatorAssignmentMapper;
    public CreateValidatorAssignmentUseCase(ValidatorAssignmentRepository validatorAssignmentRepository, ValidatorAssignmentAppMapper validatorAssignmentMapper) {
        this.validatorAssignmentRepository = validatorAssignmentRepository;
        this.validatorAssignmentMapper = validatorAssignmentMapper;
    }
    public ValidatorAssignmentResponse execute(CreateValidatorAssignmentCommand command) {
        validateCommand(command);
        ValidatorAssignment validatorAssignment = validatorAssignmentMapper.toDomain(command);
        ValidatorAssignment saved = validatorAssignmentRepository.save(validatorAssignment);
        return validatorAssignmentMapper.toResponse(saved);
    }
    private void validateCommand(CreateValidatorAssignmentCommand command) {
        if (command == null) throw new ValidatorAssignmentDomainError("CreateValidatorAssignmentCommand cannot be null");
        if (command.getValidatorId() == null || command.getValidatorId().trim().isEmpty()) throw new ValidatorAssignmentDomainError("Validator ID is required");
        if (command.getEvidenceId() == null || command.getEvidenceId().trim().isEmpty()) throw new ValidatorAssignmentDomainError("Evidence ID is required");
    }
}
