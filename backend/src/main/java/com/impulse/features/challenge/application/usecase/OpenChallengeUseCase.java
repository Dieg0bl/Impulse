package com.impulse.features.challenge.application.usecase;

import com.impulse.features.challenge.application.dto.OpenChallengeCommand;
import com.impulse.features.challenge.application.dto.ChallengeResponse;
import com.impulse.features.challenge.application.port.in.OpenChallengePort;
import com.impulse.features.challenge.application.port.out.ChallengeRepository;
import com.impulse.features.challenge.domain.Challenge;
import com.impulse.features.challenge.domain.ChallengeId;
import com.impulse.shared.error.DomainException;
import com.impulse.shared.error.ValidationException;
import com.impulse.shared.enums.Visibility;
import com.impulse.shared.utils.IdempotencyKey;
import com.impulse.infrastructure.services.IdempotencyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: OpenChallengeUseCase
 * Handles opening challenges (DRAFT → OPEN)
 * IMPULSE v1.0 specification compliant
 */
@Service
@Transactional
public class OpenChallengeUseCase implements OpenChallengePort {

    private final ChallengeRepository challengeRepository;
    private final IdempotencyService idempotencyService;

    public OpenChallengeUseCase(ChallengeRepository challengeRepository,
                               IdempotencyService idempotencyService) {
        this.challengeRepository = challengeRepository;
        this.idempotencyService = idempotencyService;
    }

    @Override
    public ChallengeResponse execute(OpenChallengeCommand command, IdempotencyKey idempotencyKey) {
        // Idempotency check
        if (idempotencyKey != null) {
            var existingResult = idempotencyService.getResult(idempotencyKey, ChallengeResponse.class);
            if (existingResult.isPresent()) {
                return existingResult.get();
            }
        }

        // Validate command
        validateCommand(command);

        // Find challenge
        ChallengeId challengeId = ChallengeId.of(command.getChallengeId());
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new DomainException("Challenge not found"));

        // Check ownership
        if (!challenge.getOwnerUserId().equals(command.getRequestingUserId())) {
            throw new DomainException("Only challenge owner can open the challenge");
        }

        try {
            // Open challenge with consent version
            challenge.open(command.getConsentVersion());

            // Set visibility if specified
            if (command.getVisibility() != null) {
                challenge.changeVisibility(command.getVisibility(), command.getConsentVersion());
            }

            // Save updated challenge
            Challenge savedChallenge = challengeRepository.save(challenge);

            // Convert to response
            ChallengeResponse response = toResponse(savedChallenge);

            // Store idempotency result if provided
            if (idempotencyKey != null) {
                idempotencyService.storeResult(idempotencyKey, response);
            }

            return response;

        } catch (Exception e) {
            if (e instanceof DomainException || e instanceof ValidationException) {
                throw e;
            }
            throw new DomainException("Failed to open challenge: " + e.getMessage());
        }
    }

    private void validateCommand(OpenChallengeCommand command) {
        if (command == null) {
            throw new ValidationException("OpenChallengeCommand cannot be null");
        }

        if (command.getChallengeId() == null || command.getChallengeId().trim().isEmpty()) {
            throw new ValidationException("Challenge ID is required");
        }

        if (command.getRequestingUserId() == null || command.getRequestingUserId() <= 0) {
            throw new ValidationException("Valid requesting user ID is required");
        }

        if (command.getVisibility() == Visibility.PUBLIC &&
            (command.getConsentVersion() == null || command.getConsentVersion().trim().isEmpty())) {
            throw new ValidationException("Consent version is required when making challenge public");
        }
    }

    private ChallengeResponse toResponse(Challenge challenge) {
        return new ChallengeResponse(
            challenge.getId().getValue(),
            challenge.getOwnerUserId(),
            challenge.getTitle(),
            challenge.getDescription(),
            challenge.getStatus(),
            challenge.getVisibility(),
            challenge.getCategory(),
            challenge.getPublicConsentVersion(),
            challenge.getOpenedAt(),
            challenge.getClosedAt(),
            challenge.getCreatedAt(),
            challenge.getUpdatedAt()
        );
    }
}
