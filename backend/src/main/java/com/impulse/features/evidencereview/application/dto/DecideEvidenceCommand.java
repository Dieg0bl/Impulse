package com.impulse.features.evidencereview.application.dto;

import com.impulse.shared.enums.EvidenceStatus;
import java.time.LocalDateTime;

/**
 * Command DTO: DecideEvidenceCommand
 * Represents a command to approve or reject evidence
 */
public class DecideEvidenceCommand {
    private final String evidenceId;
    private final Long reviewerUserId;
    private final EvidenceStatus decision;
    private final String comments;
    private final LocalDateTime timestamp;

    public DecideEvidenceCommand(String evidenceId, Long reviewerUserId,
                                EvidenceStatus decision, String comments) {
        this.evidenceId = evidenceId;
        this.reviewerUserId = reviewerUserId;
        this.decision = decision;
        this.comments = comments;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getEvidenceId() { return evidenceId; }
    public Long getReviewerUserId() { return reviewerUserId; }
    public EvidenceStatus getDecision() { return decision; }
    public String getComments() { return comments; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "DecideEvidenceCommand{" +
                "evidenceId='" + evidenceId + '\'' +
                ", reviewerUserId=" + reviewerUserId +
                ", decision=" + decision +
                ", timestamp=" + timestamp +
                '}';
    }
}
