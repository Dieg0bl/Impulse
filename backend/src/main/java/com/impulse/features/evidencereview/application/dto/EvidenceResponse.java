package com.impulse.features.evidencereview.application.dto;

import java.time.LocalDateTime;

import com.impulse.shared.enums.EvidenceStatus;
import com.impulse.shared.enums.EvidenceType;

/**
 * Response DTO: EvidenceResponse
 * Represents evidence data returned from use cases
 */
public class EvidenceResponse {
    private final String id;
    private final Long challengeId;
    private final Long participantUserId;
    private final EvidenceType type;
    private final String content;
    private final String metadata;
    private final EvidenceStatus status;
    private final Long reviewerUserId;
    private final String reviewComments;
    private final LocalDateTime reviewedAt;
    private final LocalDateTime submittedAt;
    private final LocalDateTime updatedAt;
    private final boolean isDemo;

    public EvidenceResponse(String id, Long challengeId, Long participantUserId,
                           EvidenceType type, String content, String metadata,
                           EvidenceStatus status, Long reviewerUserId, String reviewComments,
                           LocalDateTime reviewedAt, LocalDateTime submittedAt, LocalDateTime updatedAt,
                           boolean isDemo) {
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
        this.isDemo = isDemo;
    }

    // Getters
    public String getId() { return id; }
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
    public boolean isDemo() { return isDemo; }
}
