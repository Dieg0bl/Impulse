package com.impulse.features.evidencereview.domain;

import com.impulse.shared.enums.EvidenceStatus;
import com.impulse.shared.enums.EvidenceType;
import com.impulse.shared.error.DomainException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity: Evidence
 * Business logic for evidence validation workflow
 * Evidence: PENDING → APPROVED|REJECTED
 */
public class Evidence {
    private final EvidenceId id;
    private final Long challengeId;
    private final Long participantUserId;
    private EvidenceType type;
    private String content;
    private String metadata;
    private EvidenceStatus status;
    private Long reviewerUserId;
    private String reviewComments;
    private LocalDateTime reviewedAt;
    private final LocalDateTime submittedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Constructor for creation
    public Evidence(EvidenceId id, Long challengeId, Long participantUserId,
                   EvidenceType type, String content, String metadata, LocalDateTime submittedAt) {
        this.id = Objects.requireNonNull(id, "Evidence ID cannot be null");
        this.challengeId = Objects.requireNonNull(challengeId, "Challenge ID cannot be null");
        this.participantUserId = Objects.requireNonNull(participantUserId, "Participant user ID cannot be null");
        setType(type);
        setContent(content);
        this.metadata = metadata;
        this.status = EvidenceStatus.PENDING;
        this.submittedAt = Objects.requireNonNull(submittedAt, "Submitted at cannot be null");
        this.updatedAt = submittedAt;
    }

    // Constructor for reconstruction
    public Evidence(EvidenceId id, Long challengeId, Long participantUserId,
                   EvidenceType type, String content, String metadata, EvidenceStatus status,
                   Long reviewerUserId, String reviewComments, LocalDateTime reviewedAt,
                   LocalDateTime submittedAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.challengeId = challengeId;
        this.participantUserId = participantUserId;
        this.type = type;
        this.content = content;
        this.metadata = metadata;
        this.status = status;
        this.reviewerUserId = reviewerUserId;
        this.reviewComments = reviewComments;
        this.reviewedAt = reviewedAt;
        this.submittedAt = submittedAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    // Factory method
    public static Evidence submit(Long challengeId, Long participantUserId,
                                 EvidenceType type, String content, String metadata) {
        return new Evidence(
            EvidenceId.generate(),
            challengeId,
            participantUserId,
            type,
            content,
            metadata,
            LocalDateTime.now()
        );
    }

    // Business methods
    public void approve(Long reviewerUserId, String comments) {
        if (status != EvidenceStatus.PENDING) {
            throw new DomainException("Evidence can only be approved from PENDING status");
        }
        if (reviewerUserId == null) {
            throw new DomainException("Reviewer user ID is required");
        }
        if (reviewerUserId.equals(participantUserId)) {
            throw new DomainException("Participants cannot review their own evidence");
        }

        this.status = EvidenceStatus.APPROVED;
        this.reviewerUserId = reviewerUserId;
        this.reviewComments = comments;
        this.reviewedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(Long reviewerUserId, String comments) {
        if (status != EvidenceStatus.PENDING) {
            throw new DomainException("Evidence can only be rejected from PENDING status");
        }
        if (reviewerUserId == null) {
            throw new DomainException("Reviewer user ID is required");
        }
        if (reviewerUserId.equals(participantUserId)) {
            throw new DomainException("Participants cannot review their own evidence");
        }
        if (comments == null || comments.trim().isEmpty()) {
            throw new DomainException("Comments are required when rejecting evidence");
        }

        this.status = EvidenceStatus.REJECTED;
        this.reviewerUserId = reviewerUserId;
        this.reviewComments = comments.trim();
        this.reviewedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateContent(String newContent, String newMetadata) {
        if (status != EvidenceStatus.PENDING) {
            throw new DomainException("Cannot update evidence that has been reviewed");
        }

        setContent(newContent);
        this.metadata = newMetadata;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        if (status == EvidenceStatus.APPROVED) {
            throw new DomainException("Cannot delete approved evidence");
        }

        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean isPending() {
        return status == EvidenceStatus.PENDING && !isDeleted();
    }

    public boolean isReviewed() {
        return (status == EvidenceStatus.APPROVED || status == EvidenceStatus.REJECTED) && !isDeleted();
    }

    public boolean canBeUpdated() {
        return status == EvidenceStatus.PENDING && !isDeleted();
    }

    public boolean canBeReviewed() {
        return status == EvidenceStatus.PENDING && !isDeleted();
    }

    // Private validation methods
    private void setType(EvidenceType type) {
        if (type == null) {
            throw new DomainException("Evidence type is required");
        }
        this.type = type;
    }

    private void setContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new DomainException("Evidence content is required");
        }
        if (content.length() > 65535) {
            throw new DomainException("Evidence content is too long");
        }
        this.content = content.trim();
    }

    // Getters
    public EvidenceId getId() { return id; }
    public Long getChallengeId() { return challengeId; }
    public Long getParticipantUserId() { return participantUserId; }
    public EvidenceType getType() { return type; }
    public String getContent() { return content; }
    public String getMetadata() { return metadata; }
    public EvidenceStatus getStatus() { return status; }
    public Long getReviewerUserId() { return reviewerUserId; }
    public String getReviewComments() { return reviewComments; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evidence evidence = (Evidence) o;
        return Objects.equals(id, evidence.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

