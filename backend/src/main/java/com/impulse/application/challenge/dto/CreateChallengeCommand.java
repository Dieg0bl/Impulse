package com.impulse.application.challenge.dto;

import com.impulse.domain.enums.ChallengeType;
import com.impulse.domain.enums.DifficultyLevel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CreateChallengeCommand - Comando para crear un nuevo challenge
 */
public class CreateChallengeCommand {

    private final String title;
    private final String description;
    private final ChallengeType type;
    private final DifficultyLevel difficulty;
    private final Integer pointsReward;
    private final BigDecimal monetaryReward;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Integer maxParticipants;
    private final Boolean requiresEvidence;
    private final Boolean autoValidation;
    private final String createdBy; // UserId as string

    private CreateChallengeCommand(Builder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.type = builder.type;
        this.difficulty = builder.difficulty;
        this.pointsReward = builder.pointsReward;
        this.monetaryReward = builder.monetaryReward;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.maxParticipants = builder.maxParticipants;
        this.requiresEvidence = builder.requiresEvidence;
        this.autoValidation = builder.autoValidation;
        this.createdBy = builder.createdBy;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ChallengeType getType() { return type; }
    public DifficultyLevel getDifficulty() { return difficulty; }
    public Integer getPointsReward() { return pointsReward; }
    public BigDecimal getMonetaryReward() { return monetaryReward; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public Boolean getRequiresEvidence() { return requiresEvidence; }
    public Boolean getAutoValidation() { return autoValidation; }
    public String getCreatedBy() { return createdBy; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String description;
        private ChallengeType type;
        private DifficultyLevel difficulty;
        private Integer pointsReward;
        private BigDecimal monetaryReward;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer maxParticipants;
        private Boolean requiresEvidence;
        private Boolean autoValidation;
        private String createdBy;

        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder type(ChallengeType type) { this.type = type; return this; }
        public Builder difficulty(DifficultyLevel difficulty) { this.difficulty = difficulty; return this; }
        public Builder pointsReward(Integer pointsReward) { this.pointsReward = pointsReward; return this; }
        public Builder monetaryReward(BigDecimal monetaryReward) { this.monetaryReward = monetaryReward; return this; }
        public Builder startDate(LocalDateTime startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDateTime endDate) { this.endDate = endDate; return this; }
        public Builder maxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; return this; }
        public Builder requiresEvidence(Boolean requiresEvidence) { this.requiresEvidence = requiresEvidence; return this; }
        public Builder autoValidation(Boolean autoValidation) { this.autoValidation = autoValidation; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }

        public CreateChallengeCommand build() {
            return new CreateChallengeCommand(this);
        }
    }
}
