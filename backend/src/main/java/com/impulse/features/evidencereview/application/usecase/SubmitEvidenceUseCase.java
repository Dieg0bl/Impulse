package com.impulse.features.evidencereview.application.usecase;

import com.impulse.features.evidencereview.application.dto.SubmitEvidenceCommand;
import com.impulse.features.evidencereview.application.dto.EvidenceResponse;
import com.impulse.features.evidencereview.application.port.in.SubmitEvidencePort;
import com.impulse.features.evidencereview.application.port.out.EvidenceRepository;
import com.impulse.features.evidencereview.domain.Evidence;
import com.impulse.features.challenge.application.port.out.ChallengeRepository;
import com.impulse.features.challenge.domain.ChallengeId;
import com.impulse.shared.error.DomainException;
import com.impulse.shared.error.ValidationException;
import com.impulse.shared.enums.ChallengeStatus;
import com.impulse.shared.enums.EvidenceType;
import com.impulse.shared.utils.IdempotencyKey;
import com.impulse.infrastructure.services.IdempotencyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: SubmitEvidenceUseCase
 * Handles evidence submission for challenges
 * IMPULSE v1.0 specification compliant
 */
@Service
@Transactional
public class SubmitEvidenceUseCase implements SubmitEvidencePort {

    private final EvidenceRepository evidenceRepository;
    private final ChallengeRepository challengeRepository;
    private final IdempotencyService idempotencyService;

    public SubmitEvidenceUseCase(EvidenceRepository evidenceRepository,
                                ChallengeRepository challengeRepository,
                                IdempotencyService idempotencyService) {
        this.evidenceRepository = evidenceRepository;
        this.challengeRepository = challengeRepository;
        this.idempotencyService = idempotencyService;
    }

    @Override
    public EvidenceResponse execute(SubmitEvidenceCommand command, IdempotencyKey idempotencyKey) {
        // Idempotency check
        if (idempotencyKey != null) {
            var existingResult = idempotencyService.getResult(idempotencyKey, EvidenceResponse.class);
            if (existingResult.isPresent()) {
                return existingResult.get();
            }
        }

        // Validate command
        validateCommand(command);

        // Check challenge exists and is open
        var challengeId = ChallengeId.of(command.getChallengeId());
        var challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new DomainException("Challenge not found"));

        if (challenge.getStatus() != ChallengeStatus.OPEN) {
            throw new DomainException("Evidence can only be submitted to open challenges");
        }

        try {
            // Create evidence domain entity
            Evidence evidence = Evidence.submit(
                command.getChallengeId(),
                command.getParticipantUserId(),
                command.getType(),
                command.getContent(),
                command.getMetadata()
            );

            // Persist evidence
            Evidence savedEvidence = evidenceRepository.save(evidence);

            // Convert to response
            EvidenceResponse response = toResponse(savedEvidence);

            // Store idempotency result if provided
            if (idempotencyKey != null) {
                idempotencyService.storeResult(idempotencyKey, response);
            }

            return response;

        } catch (Exception e) {
            if (e instanceof DomainException || e instanceof ValidationException) {
                throw e;
            }
            throw new DomainException("Failed to submit evidence: " + e.getMessage());
        }
    }

    private void validateCommand(SubmitEvidenceCommand command) {
        if (command == null) {
            throw new ValidationException("SubmitEvidenceCommand cannot be null");
        }

        if (command.getChallengeId() == null || command.getChallengeId() <= 0) {
            throw new ValidationException("Valid challenge ID is required");
        }

        if (command.getParticipantUserId() == null || command.getParticipantUserId() <= 0) {
            throw new ValidationException("Valid participant user ID is required");
        }

        if (command.getType() == null) {
            throw new ValidationException("Evidence type is required");
        }

        if (command.getContent() == null || command.getContent().trim().isEmpty()) {
            throw new ValidationException("Evidence content is required");
        }

        if (command.getContent().length() > 65535) {
            throw new ValidationException("Evidence content is too long");
        }

        // Type-specific validations
        if (command.getType() == EvidenceType.IMAGE || command.getType() == EvidenceType.VIDEO) {
            if (command.getMetadata() == null || command.getMetadata().trim().isEmpty()) {
                throw new ValidationException("Metadata is required for image/video evidence");
            }
        }
    }

    private EvidenceResponse toResponse(Evidence evidence) {
        return new EvidenceResponse(
            evidence.getId().getValue(),
            evidence.getChallengeId(),
            evidence.getParticipantUserId(),
            evidence.getType(),
            evidence.getContent(),
            evidence.getMetadata(),
            evidence.getStatus(),
            evidence.getReviewerUserId(),
            evidence.getReviewComments(),
            evidence.getReviewedAt(),
            evidence.getSubmittedAt(),
            evidence.getUpdatedAt()
        );
    }
}
