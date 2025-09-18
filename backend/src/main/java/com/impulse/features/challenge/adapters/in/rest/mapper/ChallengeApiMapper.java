package com.impulse.features.challenge.adapters.in.rest.mapper;

import com.impulse.features.challenge.adapters.in.rest.dto.CreateChallengeRequest;
import com.impulse.features.challenge.adapters.in.rest.dto.OpenChallengeRequest;
import com.impulse.features.challenge.adapters.in.rest.dto.ChallengeApiResponse;
import com.impulse.features.challenge.application.dto.CreateChallengeCommand;
import com.impulse.features.challenge.application.dto.OpenChallengeCommand;
import com.impulse.features.challenge.application.dto.ChallengeResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper: ChallengeApiMapper
 * Maps between API DTOs and Application DTOs
 * API layer ↔ Application layer transformations
 */
@Component
public class ChallengeApiMapper {

    public CreateChallengeCommand toCreateCommand(CreateChallengeRequest request, Long userId) {
        return new CreateChallengeCommand(
            userId,
            request.getTitle(),
            request.getDescription(),
            request.getCategory()
        );
    }

    public OpenChallengeCommand toOpenCommand(String challengeId, OpenChallengeRequest request, Long userId) {
        return new OpenChallengeCommand(
            challengeId,
            userId,
            request.getVisibility(),
            request.getConsentVersion()
        );
    }

    public ChallengeApiResponse toApiResponse(ChallengeResponse response) {
        return new ChallengeApiResponse(
            response.getId(),
            response.getTitle(),
            response.getDescription(),
            response.getStatus(),
            response.getVisibility(),
            response.getCategory(),
            response.getOpenedAt(),
            response.getClosedAt(),
            response.getCreatedAt(),
            response.getUpdatedAt()
        );
    }
}
