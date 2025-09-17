package com.impulse.application.validator.usecase;

import com.impulse.application.validator.dto.ValidatorResponse;
import com.impulse.application.validator.mapper.ValidatorAppMapper;
import com.impulse.application.validator.port.ValidatorRepository;
import com.impulse.domain.validator.Validator;
import com.impulse.domain.validator.ValidatorId;
import com.impulse.domain.validator.ValidatorDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetValidatorByIdUseCase {
    private final ValidatorRepository validatorRepository;
    private final ValidatorAppMapper validatorMapper;
    public GetValidatorByIdUseCase(ValidatorRepository validatorRepository, ValidatorAppMapper validatorMapper) {
        this.validatorRepository = validatorRepository;
        this.validatorMapper = validatorMapper;
    }
    public ValidatorResponse execute(String validatorId) {
        if (validatorId == null || validatorId.trim().isEmpty()) throw new ValidatorDomainError("Validator ID cannot be null or empty");
        ValidatorId id = ValidatorId.of(validatorId);
        Validator validator = validatorRepository.findById(id)
            .orElseThrow(() -> new ValidatorDomainError("Validator not found with ID: " + validatorId));
        return validatorMapper.toResponse(validator);
    }
}
