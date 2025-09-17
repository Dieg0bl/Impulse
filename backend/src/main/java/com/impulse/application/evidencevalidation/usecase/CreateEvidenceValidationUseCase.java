package com.impulse.application.evidencevalidation.usecase;

import com.impulse.application.evidencevalidation.dto.CreateEvidenceValidationCommand;
import com.impulse.application.evidencevalidation.dto.EvidenceValidationResponse;
import com.impulse.application.evidencevalidation.mapper.EvidenceValidationAppMapper;
import com.impulse.application.evidencevalidation.port.EvidenceValidationRepository;
import com.impulse.domain.evidencevalidation.EvidenceValidation;
import com.impulse.domain.evidencevalidation.EvidenceValidationDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateEvidenceValidationUseCase {
    private final EvidenceValidationRepository evidenceValidationRepository;
    private final EvidenceValidationAppMapper evidenceValidationMapper;
    public CreateEvidenceValidationUseCase(EvidenceValidationRepository evidenceValidationRepository, EvidenceValidationAppMapper evidenceValidationMapper) {
        this.evidenceValidationRepository = evidenceValidationRepository;
        this.evidenceValidationMapper = evidenceValidationMapper;
    }
    public EvidenceValidationResponse execute(CreateEvidenceValidationCommand command) {
        validateCommand(command);
        EvidenceValidation evidenceValidation = evidenceValidationMapper.toDomain(command);
        EvidenceValidation saved = evidenceValidationRepository.save(evidenceValidation);
        return evidenceValidationMapper.toResponse(saved);
    }
    private void validateCommand(CreateEvidenceValidationCommand command) {
        if (command == null) throw new EvidenceValidationDomainError("CreateEvidenceValidationCommand cannot be null");
        if (command.getEvidenceId() == null || command.getEvidenceId().trim().isEmpty()) throw new EvidenceValidationDomainError("Evidence ID is required");
        if (command.getValidatorId() == null || command.getValidatorId().trim().isEmpty()) throw new EvidenceValidationDomainError("Validator ID is required");
    }
}
