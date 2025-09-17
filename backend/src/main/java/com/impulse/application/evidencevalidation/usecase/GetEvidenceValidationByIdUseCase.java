package com.impulse.application.evidencevalidation.usecase;

import com.impulse.application.evidencevalidation.dto.EvidenceValidationResponse;
import com.impulse.application.evidencevalidation.mapper.EvidenceValidationAppMapper;
import com.impulse.application.evidencevalidation.port.EvidenceValidationRepository;
import com.impulse.domain.evidencevalidation.EvidenceValidation;
import com.impulse.domain.evidencevalidation.EvidenceValidationId;
import com.impulse.domain.evidencevalidation.EvidenceValidationDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetEvidenceValidationByIdUseCase {
    private final EvidenceValidationRepository evidenceValidationRepository;
    private final EvidenceValidationAppMapper evidenceValidationMapper;
    public GetEvidenceValidationByIdUseCase(EvidenceValidationRepository evidenceValidationRepository, EvidenceValidationAppMapper evidenceValidationMapper) {
        this.evidenceValidationRepository = evidenceValidationRepository;
        this.evidenceValidationMapper = evidenceValidationMapper;
    }
    public EvidenceValidationResponse execute(String evidenceValidationId) {
        if (evidenceValidationId == null || evidenceValidationId.trim().isEmpty()) throw new EvidenceValidationDomainError("EvidenceValidation ID cannot be null or empty");
        EvidenceValidationId id = EvidenceValidationId.of(evidenceValidationId);
        EvidenceValidation evidenceValidation = evidenceValidationRepository.findById(id)
            .orElseThrow(() -> new EvidenceValidationDomainError("EvidenceValidation not found with ID: " + evidenceValidationId));
        return evidenceValidationMapper.toResponse(evidenceValidation);
    }
}
