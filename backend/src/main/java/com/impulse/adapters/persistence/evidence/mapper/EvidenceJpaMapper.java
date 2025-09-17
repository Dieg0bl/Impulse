package com.impulse.adapters.persistence.evidence.mapper;

import com.impulse.adapters.persistence.evidence.entity.EvidenceJpaEntity;
import com.impulse.domain.evidence.Evidence;
import com.impulse.domain.evidence.EvidenceId;
import com.impulse.domain.user.UserId;
import com.impulse.domain.challenge.ChallengeId;
import org.springframework.stereotype.Component;

/**
 * EvidenceJpaMapper - Mapper entre entidad JPA y entidad de dominio
 */
@Component
public class EvidenceJpaMapper {

    public EvidenceJpaEntity toJpaEntity(Evidence evidence) {
        if (evidence == null) return null;
        EvidenceJpaEntity entity = new EvidenceJpaEntity();
        entity.setId(evidence.getId() != null ? evidence.getId().asLong() : null);
        entity.setTitle(evidence.getTitle());
        entity.setDescription(evidence.getDescription());
        entity.setType(evidence.getType());
        entity.setStatus(evidence.getStatus());
        entity.setFileUrl(evidence.getFileUrl());
        entity.setFileName(evidence.getFileName());
        entity.setFileSize(evidence.getFileSize());
        entity.setMimeType(evidence.getMimeType());
        entity.setSubmissionDate(evidence.getSubmissionDate());
        entity.setValidationDeadline(evidence.getValidationDeadline());
        entity.setUserId(evidence.getUserId() != null ? evidence.getUserId().asLong() : null);
        entity.setChallengeId(evidence.getChallengeId() != null ? evidence.getChallengeId().asLong() : null);
        entity.setCreatedAt(evidence.getCreatedAt());
        entity.setUpdatedAt(evidence.getUpdatedAt());
        return entity;
    }

    public Evidence toDomainEntity(EvidenceJpaEntity entity) {
        if (entity == null) return null;
        return Evidence.builder()
                .id(entity.getId() != null ? EvidenceId.of(entity.getId()) : null)
                .title(entity.getTitle())
                .description(entity.getDescription())
                .type(entity.getType())
                .status(entity.getStatus())
                .fileUrl(entity.getFileUrl())
                .fileName(entity.getFileName())
                .fileSize(entity.getFileSize())
                .mimeType(entity.getMimeType())
                .submissionDate(entity.getSubmissionDate())
                .validationDeadline(entity.getValidationDeadline())
                .userId(entity.getUserId() != null ? UserId.of(entity.getUserId()) : null)
                .challengeId(entity.getChallengeId() != null ? ChallengeId.of(entity.getChallengeId()) : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}


