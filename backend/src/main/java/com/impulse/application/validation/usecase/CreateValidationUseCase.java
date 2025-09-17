package com.impulse.application.validation.usecase;

import com.impulse.application.validation.dto.CreateValidationCommand;
import com.impulse.application.validation.dto.ValidationResponse;
import com.impulse.application.validation.mapper.ValidationAppMapper;
import com.impulse.application.validation.port.ValidationRepository;
import com.impulse.domain.validation.Validation;
import com.impulse.domain.validation.ValidationDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateValidationUseCase {
    private final ValidationRepository validationRepository;
    private final ValidationAppMapper validationMapper;
    public CreateValidationUseCase(ValidationRepository validationRepository, ValidationAppMapper validationMapper) {
        this.validationRepository = validationRepository;
        this.validationMapper = validationMapper;
    }
    public ValidationResponse execute(CreateValidationCommand command) {
        validateCommand(command);
        Validation validation = validationMapper.toDomain(command);
        Validation saved = validationRepository.save(validation);
        return validationMapper.toResponse(saved);
    }
    private void validateCommand(CreateValidationCommand command) {
        if (command == null) throw new ValidationDomainError("CreateValidationCommand cannot be null");
        if (command.getEvidenceId() == null || command.getEvidenceId().trim().isEmpty()) throw new ValidationDomainError("EvidenceId is required");
        if (command.getValidatorUserId() == null || command.getValidatorUserId().trim().isEmpty()) throw new ValidationDomainError("ValidatorUserId is required");
    }
}
