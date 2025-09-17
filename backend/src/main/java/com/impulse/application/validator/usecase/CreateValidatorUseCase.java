package com.impulse.application.validator.usecase;

import com.impulse.application.validator.dto.CreateValidatorCommand;
import com.impulse.application.validator.dto.ValidatorResponse;
import com.impulse.application.validator.mapper.ValidatorAppMapper;
import com.impulse.application.validator.port.ValidatorRepository;
import com.impulse.domain.validator.Validator;
import com.impulse.domain.validator.ValidatorDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateValidatorUseCase {
    private final ValidatorRepository validatorRepository;
    private final ValidatorAppMapper validatorMapper;
    public CreateValidatorUseCase(ValidatorRepository validatorRepository, ValidatorAppMapper validatorMapper) {
        this.validatorRepository = validatorRepository;
        this.validatorMapper = validatorMapper;
    }
    public ValidatorResponse execute(CreateValidatorCommand command) {
        validateCommand(command);
        Validator validator = validatorMapper.toDomain(command);
        Validator saved = validatorRepository.save(validator);
        return validatorMapper.toResponse(saved);
    }
    private void validateCommand(CreateValidatorCommand command) {
        if (command == null) throw new ValidatorDomainError("CreateValidatorCommand cannot be null");
        if (command.getName() == null || command.getName().trim().isEmpty()) throw new ValidatorDomainError("Name is required");
        if (command.getEmail() == null || command.getEmail().trim().isEmpty()) throw new ValidatorDomainError("Email is required");
    }
}
