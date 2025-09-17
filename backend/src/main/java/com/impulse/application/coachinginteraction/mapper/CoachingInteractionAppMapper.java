package com.impulse.application.coachinginteraction.mapper;

import com.impulse.application.coachinginteraction.dto.CreateCoachingInteractionCommand;
import com.impulse.application.coachinginteraction.dto.CoachingInteractionResponse;
import com.impulse.domain.coachinginteraction.CoachingInteraction;
import org.springframework.stereotype.Component;

@Component
public class CoachingInteractionAppMapper {
    public CoachingInteraction toDomain(CreateCoachingInteractionCommand command) {
        return CoachingInteraction.builder()
                .id(null)
                .sessionId(command.getSessionId())
                .type(command.getType())
                .content(command.getContent())
                .timestamp(command.getTimestamp())
                .userId(command.getUserId())
                .metadata(command.getMetadata())
                .build();
    }
    public CoachingInteractionResponse toResponse(CoachingInteraction coachingInteraction) {
        return CoachingInteractionResponse.builder()
                .id(coachingInteraction.getId() != null ? coachingInteraction.getId().getValue() : null)
                .sessionId(coachingInteraction.getSessionId())
                .type(coachingInteraction.getType())
                .content(coachingInteraction.getContent())
                .timestamp(coachingInteraction.getTimestamp())
                .userId(coachingInteraction.getUserId())
                .metadata(coachingInteraction.getMetadata())
                .build();
    }
}
