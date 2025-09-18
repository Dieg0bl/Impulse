package com.impulse.features.evidencereview.application.dto;

import com.impulse.shared.enums.EvidenceType;
import java.time.LocalDateTime;

/**
 * Command DTO: SubmitEvidenceCommand
 * Represents a command to submit evidence for a challenge
 */
public class SubmitEvidenceCommand {
    private final Long challengeId;
    private final Long participantUserId;
    private final EvidenceType type;
    private final String content;
    private final String metadata;
    private final LocalDateTime timestamp;

    public SubmitEvidenceCommand(Long challengeId, Long participantUserId,
                                EvidenceType type, String content, String metadata) {
        this.challengeId = challengeId;
        this.participantUserId = participantUserId;
        this.type = type;
        this.content = content;
        this.metadata = metadata;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public Long getChallengeId() { return challengeId; }
    public Long getParticipantUserId() { return participantUserId; }
    public EvidenceType getType() { return type; }
    public String getContent() { return content; }
    public String getMetadata() { return metadata; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "SubmitEvidenceCommand{" +
                "challengeId=" + challengeId +
                ", participantUserId=" + participantUserId +
                ", type=" + type +
                ", timestamp=" + timestamp +
                '}';
    }
}
