package com.impulse.application.challenge.dto;

import com.impulse.domain.enums.ChallengeStatus;
import com.impulse.domain.enums.ChallengeType;
import com.impulse.domain.enums.DifficultyLevel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ChallengeResponse - DTO de respuesta para Challenge
 */
public class ChallengeResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final ChallengeType type;
    private final DifficultyLevel difficulty;
    private final ChallengeStatus status;
    private final Integer pointsReward;
    private final BigDecimal monetaryReward;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Integer maxParticipants;
    private final Integer currentParticipants;
    private final Boolean requiresEvidence;
    private final Boolean autoValidation;
    private final String createdBy;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private ChallengeResponse(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.type = builder.type;
        this.difficulty = builder.difficulty;
        this.status = builder.status;
        this.pointsReward = builder.pointsReward;
        this.monetaryReward = builder.monetaryReward;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.maxParticipants = builder.maxParticipants;
        this.currentParticipants = builder.currentParticipants;
        this.requiresEvidence = builder.requiresEvidence;
        this.autoValidation = builder.autoValidation;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ChallengeType getType() { return type; }
    public DifficultyLevel getDifficulty() { return difficulty; }
    public ChallengeStatus getStatus() { return status; }
    public Integer getPointsReward() { return pointsReward; }
    public BigDecimal getMonetaryReward() { return monetaryReward; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public Integer getCurrentParticipants() { return currentParticipants; }
    public Boolean getRequiresEvidence() { return requiresEvidence; }
    public Boolean getAutoValidation() { return autoValidation; }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private ChallengeType type;
        private DifficultyLevel difficulty;
        private ChallengeStatus status;
        private Integer pointsReward;
        private BigDecimal monetaryReward;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer maxParticipants;
        private Integer currentParticipants;
        private Boolean requiresEvidence;
        private Boolean autoValidation;
        private String createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder type(ChallengeType type) { this.type = type; return this; }
        public Builder difficulty(DifficultyLevel difficulty) { this.difficulty = difficulty; return this; }
        public Builder status(ChallengeStatus status) { this.status = status; return this; }
        public Builder pointsReward(Integer pointsReward) { this.pointsReward = pointsReward; return this; }
        public Builder monetaryReward(BigDecimal monetaryReward) { this.monetaryReward = monetaryReward; return this; }
        public Builder startDate(LocalDateTime startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDateTime endDate) { this.endDate = endDate; return this; }
        public Builder maxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; return this; }
        public Builder currentParticipants(Integer currentParticipants) { this.currentParticipants = currentParticipants; return this; }
        public Builder requiresEvidence(Boolean requiresEvidence) { this.requiresEvidence = requiresEvidence; return this; }
        public Builder autoValidation(Boolean autoValidation) { this.autoValidation = autoValidation; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ChallengeResponse build() {
            return new ChallengeResponse(this);
        }
    }
}
