package com.impulse.application.challenge.usecase;

import com.impulse.application.challenge.dto.CreateChallengeCommand;
import com.impulse.application.challenge.dto.ChallengeResponse;
import com.impulse.application.challenge.mapper.ChallengeAppMapper;
import com.impulse.application.challenge.port.ChallengeRepository;
import com.impulse.domain.challenge.Challenge;
import com.impulse.domain.challenge.ChallengeDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CreateChallengeUseCase - Caso de uso para crear un nuevo challenge
 */
@Service
@Transactional
public class CreateChallengeUseCase {

    private final ChallengeRepository challengeRepository;
    private final ChallengeAppMapper challengeMapper;

    public CreateChallengeUseCase(ChallengeRepository challengeRepository,
                                 ChallengeAppMapper challengeMapper) {
        this.challengeRepository = challengeRepository;
        this.challengeMapper = challengeMapper;
    }

    /**
     * Ejecuta el caso de uso para crear un nuevo challenge
     */
    public ChallengeResponse execute(CreateChallengeCommand command) {
        validateCommand(command);

        Challenge challenge = challengeMapper.toDomain(command);
        Challenge savedChallenge = challengeRepository.save(challenge);

        return challengeMapper.toResponse(savedChallenge);
    }

    private void validateCommand(CreateChallengeCommand command) {
        if (command == null) {
            throw new ChallengeDomainError("CreateChallengeCommand cannot be null");
        }

        validateRequiredFields(command);
        validateDateLogic(command);
        validateNumericFields(command);
    }

    private void validateRequiredFields(CreateChallengeCommand command) {
        if (command.getTitle() == null || command.getTitle().trim().isEmpty()) {
            throw new ChallengeDomainError("Challenge title is required");
        }

        if (command.getDescription() == null || command.getDescription().trim().isEmpty()) {
            throw new ChallengeDomainError("Challenge description is required");
        }

        if (command.getType() == null) {
            throw new ChallengeDomainError("Challenge type is required");
        }

        if (command.getDifficulty() == null) {
            throw new ChallengeDomainError("Challenge difficulty is required");
        }

        if (command.getCreatedBy() == null || command.getCreatedBy().trim().isEmpty()) {
            throw new ChallengeDomainError("Challenge creator is required");
        }
    }

    private void validateDateLogic(CreateChallengeCommand command) {
        if (command.getStartDate() != null && command.getEndDate() != null
            && command.getStartDate().isAfter(command.getEndDate())) {
            throw new ChallengeDomainError("Start date cannot be after end date");
        }
    }

    private void validateNumericFields(CreateChallengeCommand command) {
        if (command.getMaxParticipants() != null && command.getMaxParticipants() <= 0) {
            throw new ChallengeDomainError("Max participants must be greater than 0");
        }

        if (command.getPointsReward() != null && command.getPointsReward() < 0) {
            throw new ChallengeDomainError("Points reward cannot be negative");
        }
    }
}
