package com.impulse.adapters.http.challenge.mapper;

import com.impulse.adapters.http.challenge.dto.CreateChallengeRequestDto;
import com.impulse.adapters.http.challenge.dto.ChallengeResponseDto;
import com.impulse.application.challenge.dto.CreateChallengeCommand;
import com.impulse.application.challenge.dto.ChallengeResponse;
import org.springframework.stereotype.Component;

/**
 * ChallengeApiMapper - Mapper entre DTOs de API y DTOs de aplicación
 */
@Component
public class ChallengeApiMapper {

    /**
     * Convierte CreateChallengeRequestDto a CreateChallengeCommand
     */
    public CreateChallengeCommand toCommand(CreateChallengeRequestDto requestDto, String createdBy) {
        if (requestDto == null) {
            return null;
        }

        return CreateChallengeCommand.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .type(requestDto.getType())
                .difficulty(requestDto.getDifficulty())
                .pointsReward(requestDto.getPointsReward())
                .monetaryReward(requestDto.getMonetaryReward())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .maxParticipants(requestDto.getMaxParticipants())
                .requiresEvidence(requestDto.getRequiresEvidence())
                .autoValidation(requestDto.getAutoValidation())
                .createdBy(createdBy)
                .build();
    }

    /**
     * Convierte ChallengeResponse a ChallengeResponseDto
     */
    public ChallengeResponseDto toDto(ChallengeResponse response) {
        if (response == null) {
            return null;
        }

        ChallengeResponseDto dto = new ChallengeResponseDto();
        dto.setId(response.getId());
        dto.setTitle(response.getTitle());
        dto.setDescription(response.getDescription());
        dto.setType(response.getType());
        dto.setDifficulty(response.getDifficulty());
        dto.setStatus(response.getStatus());
        dto.setPointsReward(response.getPointsReward());
        dto.setMonetaryReward(response.getMonetaryReward());
        dto.setStartDate(response.getStartDate());
        dto.setEndDate(response.getEndDate());
        dto.setMaxParticipants(response.getMaxParticipants());
        dto.setCurrentParticipants(response.getCurrentParticipants());
        dto.setRequiresEvidence(response.getRequiresEvidence());
        dto.setAutoValidation(response.getAutoValidation());
        dto.setCreatedBy(response.getCreatedBy());
        dto.setCreatedAt(response.getCreatedAt());
        dto.setUpdatedAt(response.getUpdatedAt());

        return dto;
    }
}
