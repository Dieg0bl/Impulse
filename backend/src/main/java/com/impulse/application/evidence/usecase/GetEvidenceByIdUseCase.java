package com.impulse.application.evidence.usecase;

import com.impulse.application.evidence.dto.EvidenceResponse;
import com.impulse.application.evidence.mapper.EvidenceAppMapper;
import com.impulse.application.evidence.port.EvidenceRepository;
import com.impulse.domain.evidence.Evidence;
import com.impulse.domain.evidence.EvidenceId;
import com.impulse.domain.evidence.EvidenceDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * GetEvidenceByIdUseCase - Caso de uso para obtener una evidencia por ID
 */
@Service
@Transactional(readOnly = true)
public class GetEvidenceByIdUseCase {

    private final EvidenceRepository evidenceRepository;
    private final EvidenceAppMapper evidenceMapper;

    public GetEvidenceByIdUseCase(EvidenceRepository evidenceRepository,
                                 EvidenceAppMapper evidenceMapper) {
        this.evidenceRepository = evidenceRepository;
        this.evidenceMapper = evidenceMapper;
    }

    /**
     * Ejecuta el caso de uso para obtener una evidencia por ID
     */
    public EvidenceResponse execute(String evidenceId) {
        if (evidenceId == null || evidenceId.trim().isEmpty()) {
            throw new EvidenceDomainError("Evidence ID cannot be null or empty");
        }

        EvidenceId id = EvidenceId.of(evidenceId);
        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new EvidenceDomainError("Evidence not found with ID: " + evidenceId));

        return evidenceMapper.toResponse(evidence);
    }
}
