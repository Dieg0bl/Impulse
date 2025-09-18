package com.impulse.features.challenge.application.usecase;

import com.impulse.features.challenge.application.dto.CreateChallengeCommand;
import com.impulse.features.challenge.application.dto.ChallengeResponse;
import com.impulse.features.challenge.application.port.in.CreateChallengePort;
import com.impulse.features.challenge.application.port.out.ChallengeRepository;
import com.impulse.features.challenge.domain.Challenge;
import com.impulse.shared.error.DomainException;
import com.impulse.shared.error.ValidationException;
import com.impulse.shared.utils.IdempotencyKey;
import com.impulse.infrastructure.services.IdempotencyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: CreateChallengeUseCase
 * Handles creation of new challenges with business validation
 * IMPULSE v1.0 specification compliant
 */
@Service
@Transactional
public class CreateChallengeUseCase implements CreateChallengePort {

    private final ChallengeRepository challengeRepository;
    private final IdempotencyService idempotencyService;

    public CreateChallengeUseCase(ChallengeRepository challengeRepository,
                                 IdempotencyService idempotencyService) {
        this.challengeRepository = challengeRepository;
        this.idempotencyService = idempotencyService;
    }

    @Override
    public ChallengeResponse execute(CreateChallengeCommand command, IdempotencyKey idempotencyKey) {
        // Idempotency check
        if (idempotencyKey != null) {
            var existingResult = idempotencyService.getResult(idempotencyKey, ChallengeResponse.class);
            if (existingResult.isPresent()) {
                return existingResult.get();
            }
        }

        // Validate command
        validateCommand(command);

        try {
            // Create challenge domain entity
            Challenge challenge = Challenge.create(
                command.getOwnerUserId(),
                command.getTitle(),
                command.getDescription(),
                command.getCategory()
            );

            // Persist challenge
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
            throw new DomainException("Failed to create challenge: " + e.getMessage());
        }
    }

    private void validateCommand(CreateChallengeCommand command) {
        if (command == null) {
            throw new ValidationException("CreateChallengeCommand cannot be null");
        }

        if (command.getOwnerUserId() == null || command.getOwnerUserId() <= 0) {
            throw new ValidationException("Valid owner user ID is required");
        }

        if (command.getTitle() == null || command.getTitle().trim().isEmpty()) {
            throw new ValidationException("Challenge title is required");
        }

        if (command.getTitle().length() > 200) {
            throw new ValidationException("Challenge title cannot exceed 200 characters");
        }

        if (command.getDescription() != null && command.getDescription().length() > 65535) {
            throw new ValidationException("Challenge description is too long");
        }

        if (command.getCategory() != null && command.getCategory().length() > 100) {
            throw new ValidationException("Challenge category cannot exceed 100 characters");
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
