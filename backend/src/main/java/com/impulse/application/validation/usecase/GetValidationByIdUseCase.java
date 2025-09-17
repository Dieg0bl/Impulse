package com.impulse.application.validation.usecase;

import com.impulse.application.validation.dto.ValidationResponse;
import com.impulse.application.validation.mapper.ValidationAppMapper;
import com.impulse.application.validation.port.ValidationRepository;
import com.impulse.domain.validation.Validation;
import com.impulse.domain.validation.ValidationId;
import com.impulse.domain.validation.ValidationDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetValidationByIdUseCase {
    private final ValidationRepository validationRepository;
    private final ValidationAppMapper validationMapper;
    public GetValidationByIdUseCase(ValidationRepository validationRepository, ValidationAppMapper validationMapper) {
        this.validationRepository = validationRepository;
        this.validationMapper = validationMapper;
    }
    public ValidationResponse execute(Long validationId) {
        if (validationId == null) throw new ValidationDomainError("Validation ID cannot be null");
        ValidationId id = ValidationId.of(validationId);
        Validation validation = validationRepository.findById(id)
            .orElseThrow(() -> new ValidationDomainError("Validation not found with ID: " + validationId));
        return validationMapper.toResponse(validation);
    }
}
