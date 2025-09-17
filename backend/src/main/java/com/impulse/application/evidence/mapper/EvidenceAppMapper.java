package com.impulse.application.evidence.mapper;

import com.impulse.application.evidence.dto.CreateEvidenceCommand;
import com.impulse.application.evidence.dto.EvidenceResponse;
import com.impulse.domain.evidence.Evidence;
import com.impulse.domain.evidence.EvidenceId;
import com.impulse.domain.user.UserId;
import com.impulse.domain.challenge.ChallengeId;
import org.springframework.stereotype.Component;

/**
 * EvidenceAppMapper - Mapper entre DTOs de aplicación y entidades de dominio
 */
@Component
public class EvidenceAppMapper {

    /**
     * Convierte CreateEvidenceCommand a entidad de dominio Evidence
     */
    public Evidence toDomain(CreateEvidenceCommand command) {
        if (command == null) {
            return null;
        }

        UserId userId = UserId.of(Long.parseLong(command.getUserId()));
        ChallengeId challengeId = ChallengeId.of(Long.parseLong(command.getChallengeId()));

        Evidence evidence = Evidence.createNew(
                command.getTitle(),
                command.getDescription(),
                command.getType(),
                userId,
                challengeId
        );

        if (command.getValidationDeadline() != null) {
            evidence.setValidationDeadline(command.getValidationDeadline());
        }

        return evidence;
    }

    /**
     * Convierte entidad de dominio Evidence a EvidenceResponse
     */
    public EvidenceResponse toResponse(Evidence evidence) {
        if (evidence == null) {
            return null;
        }

        return EvidenceResponse.builder()
                .id(evidence.getId().asString())
                .title(evidence.getTitle())
                .description(evidence.getDescription())
                .type(evidence.getType())
                .status(evidence.getStatus())
                .fileUrl(evidence.getFileUrl())
                .fileName(evidence.getFileName())
                .fileSize(evidence.getFileSize())
                .mimeType(evidence.getMimeType())
                .submissionDate(evidence.getSubmissionDate())
                .validationDeadline(evidence.getValidationDeadline())
                .userId(evidence.getUserId().asLong().toString())
                .challengeId(evidence.getChallengeId().asLong().toString())
                .createdAt(evidence.getCreatedAt())
                .updatedAt(evidence.getUpdatedAt())
                .build();
    }
}
