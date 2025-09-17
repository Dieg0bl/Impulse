package com.impulse.application.challenge.mapper;

import com.impulse.application.challenge.dto.CreateChallengeCommand;
import com.impulse.application.challenge.dto.ChallengeResponse;
import com.impulse.domain.challenge.Challenge;
import com.impulse.domain.challenge.ChallengeId;
import com.impulse.domain.user.UserId;
import com.impulse.domain.enums.ChallengeStatus;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

/**
 * ChallengeAppMapper - Mapper entre DTOs de aplicación y entidades de dominio
 */
@Component
public class ChallengeAppMapper {

    /**
     * Convierte CreateChallengeCommand a entidad de dominio Challenge
     */
    public Challenge toDomain(CreateChallengeCommand command) {
        if (command == null) {
            return null;
        }

        return Challenge.builder()
                .id(ChallengeId.generate())
                .title(command.getTitle())
                .description(command.getDescription())
                .type(command.getType())
                .difficulty(command.getDifficulty())
                .status(ChallengeStatus.PENDING)
                .pointsReward(command.getPointsReward())
                .monetaryReward(command.getMonetaryReward())
                .startDate(command.getStartDate())
                .endDate(command.getEndDate())
                .maxParticipants(command.getMaxParticipants())
                .currentParticipants(0)
                .requiresEvidence(command.getRequiresEvidence())
                .autoValidation(command.getAutoValidation())
                .createdBy(UserId.of(Long.parseLong(command.getCreatedBy())))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Convierte entidad de dominio Challenge a ChallengeResponse
     */
    public ChallengeResponse toResponse(Challenge challenge) {
        if (challenge == null) {
            return null;
        }

        return ChallengeResponse.builder()
                .id(challenge.getId().asLong())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .type(challenge.getType())
                .difficulty(challenge.getDifficulty())
                .status(challenge.getStatus())
                .pointsReward(challenge.getPointsReward())
                .monetaryReward(challenge.getMonetaryReward())
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .maxParticipants(challenge.getMaxParticipants())
                .currentParticipants(challenge.getCurrentParticipants())
                .requiresEvidence(challenge.getRequiresEvidence())
                .autoValidation(challenge.getAutoValidation())
                .createdBy(challenge.getCreatedBy().asLong().toString())
                .createdAt(challenge.getCreatedAt())
                .updatedAt(challenge.getUpdatedAt())
                .build();
    }
}
