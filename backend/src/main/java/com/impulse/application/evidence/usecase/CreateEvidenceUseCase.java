package com.impulse.application.evidence.usecase;

import com.impulse.application.evidence.dto.CreateEvidenceCommand;
import com.impulse.application.evidence.dto.EvidenceResponse;
import com.impulse.application.evidence.mapper.EvidenceAppMapper;
import com.impulse.application.evidence.port.EvidenceRepository;
import com.impulse.domain.evidence.Evidence;
import com.impulse.domain.evidence.EvidenceDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CreateEvidenceUseCase - Caso de uso para crear una nueva evidencia
 */
@Service
@Transactional
public class CreateEvidenceUseCase {

    private final EvidenceRepository evidenceRepository;
    private final EvidenceAppMapper evidenceMapper;

    public CreateEvidenceUseCase(EvidenceRepository evidenceRepository,
                                EvidenceAppMapper evidenceMapper) {
        this.evidenceRepository = evidenceRepository;
        this.evidenceMapper = evidenceMapper;
    }

    /**
     * Ejecuta el caso de uso para crear una nueva evidencia
     */
    public EvidenceResponse execute(CreateEvidenceCommand command) {
        validateCommand(command);

        Evidence evidence = evidenceMapper.toDomain(command);
        Evidence savedEvidence = evidenceRepository.save(evidence);

        return evidenceMapper.toResponse(savedEvidence);
    }

    private void validateCommand(CreateEvidenceCommand command) {
        if (command == null) {
            throw new EvidenceDomainError("CreateEvidenceCommand cannot be null");
        }

        validateRequiredFields(command);
        validateBusinessRules(command);
    }

    private void validateRequiredFields(CreateEvidenceCommand command) {
        if (command.getTitle() == null || command.getTitle().trim().isEmpty()) {
            throw new EvidenceDomainError("Evidence title is required");
        }

        if (command.getType() == null) {
            throw new EvidenceDomainError("Evidence type is required");
        }

        if (command.getUserId() == null || command.getUserId().trim().isEmpty()) {
            throw new EvidenceDomainError("User ID is required");
        }

        if (command.getChallengeId() == null || command.getChallengeId().trim().isEmpty()) {
            throw new EvidenceDomainError("Challenge ID is required");
        }
    }

    private void validateBusinessRules(CreateEvidenceCommand command) {
        if (command.getTitle().length() > 500) {
            throw new EvidenceDomainError("Evidence title cannot exceed 500 characters");
        }

        try {
            Long.parseLong(command.getUserId());
        } catch (NumberFormatException e) {
            throw new EvidenceDomainError("Invalid user ID format");
        }

        try {
            Long.parseLong(command.getChallengeId());
        } catch (NumberFormatException e) {
            throw new EvidenceDomainError("Invalid challenge ID format");
        }
    }
}
