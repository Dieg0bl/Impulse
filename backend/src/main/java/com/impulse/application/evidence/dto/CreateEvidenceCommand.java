package com.impulse.application.evidence.dto;

import com.impulse.domain.enums.EvidenceType;
import java.time.LocalDateTime;

/**
 * CreateEvidenceCommand - Comando para crear una nueva evidencia
 */
public class CreateEvidenceCommand {

    private final String title;
    private final String description;
    private final EvidenceType type;
    private final String userId; // String representation of UserId
    private final String challengeId; // String representation of ChallengeId
    private final LocalDateTime validationDeadline;

    private CreateEvidenceCommand(Builder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.type = builder.type;
        this.userId = builder.userId;
        this.challengeId = builder.challengeId;
        this.validationDeadline = builder.validationDeadline;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public EvidenceType getType() { return type; }
    public String getUserId() { return userId; }
    public String getChallengeId() { return challengeId; }
    public LocalDateTime getValidationDeadline() { return validationDeadline; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String description;
        private EvidenceType type;
        private String userId;
        private String challengeId;
        private LocalDateTime validationDeadline;

        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder type(EvidenceType type) { this.type = type; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder challengeId(String challengeId) { this.challengeId = challengeId; return this; }
        public Builder validationDeadline(LocalDateTime validationDeadline) { this.validationDeadline = validationDeadline; return this; }

        public CreateEvidenceCommand build() {
            return new CreateEvidenceCommand(this);
        }
    }
}
