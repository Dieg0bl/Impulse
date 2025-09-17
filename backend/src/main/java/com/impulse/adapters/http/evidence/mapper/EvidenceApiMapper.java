package com.impulse.adapters.http.evidence.mapper;

import com.impulse.adapters.http.evidence.dto.CreateEvidenceRequestDto;
import com.impulse.adapters.http.evidence.dto.EvidenceResponseDto;
import com.impulse.application.evidence.dto.CreateEvidenceCommand;
import com.impulse.application.evidence.dto.EvidenceResponse;
import org.springframework.stereotype.Component;

/**
 * EvidenceApiMapper - Mapper entre DTOs de API y DTOs de aplicación
 */
@Component
public class EvidenceApiMapper {

    /**
     * Convierte CreateEvidenceRequestDto a CreateEvidenceCommand
     */
    public CreateEvidenceCommand toCommand(CreateEvidenceRequestDto requestDto, String userId) {
        if (requestDto == null) {
            return null;
        }

        return CreateEvidenceCommand.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .type(requestDto.getType())
                .userId(userId)
                .challengeId(requestDto.getChallengeId())
                .validationDeadline(requestDto.getValidationDeadline())
                .build();
    }

    /**
     * Convierte EvidenceResponse a EvidenceResponseDto
     */
    public EvidenceResponseDto toDto(EvidenceResponse response) {
        if (response == null) {
            return null;
        }

        EvidenceResponseDto dto = new EvidenceResponseDto();
        dto.setId(response.getId());
        dto.setTitle(response.getTitle());
        dto.setDescription(response.getDescription());
        dto.setType(response.getType());
        dto.setStatus(response.getStatus());
        dto.setFileUrl(response.getFileUrl());
        dto.setFileName(response.getFileName());
        dto.setFileSize(response.getFileSize());
        dto.setMimeType(response.getMimeType());
        dto.setSubmissionDate(response.getSubmissionDate());
        dto.setValidationDeadline(response.getValidationDeadline());
        dto.setUserId(response.getUserId());
        dto.setChallengeId(response.getChallengeId());
        dto.setCreatedAt(response.getCreatedAt());
        dto.setUpdatedAt(response.getUpdatedAt());

        return dto;
    }
}
