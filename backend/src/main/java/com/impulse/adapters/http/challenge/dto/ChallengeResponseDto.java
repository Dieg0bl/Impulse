package com.impulse.adapters.http.challenge.dto;

import com.impulse.domain.enums.ChallengeStatus;
import com.impulse.domain.enums.ChallengeType;
import com.impulse.domain.enums.DifficultyLevel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ChallengeResponseDto - DTO de response para Challenge
 */
public class ChallengeResponseDto {

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

    // Constructor vacío requerido por Jackson para deserialización
    public ChallengeResponseDto() {
        // Constructor vacío para serialización/deserialización JSON
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ChallengeType getType() { return type; }
    public void setType(ChallengeType type) { this.type = type; }

    public DifficultyLevel getDifficulty() { return difficulty; }
    public void setDifficulty(DifficultyLevel difficulty) { this.difficulty = difficulty; }

    public ChallengeStatus getStatus() { return status; }
    public void setStatus(ChallengeStatus status) { this.status = status; }

    public Integer getPointsReward() { return pointsReward; }
    public void setPointsReward(Integer pointsReward) { this.pointsReward = pointsReward; }

    public BigDecimal getMonetaryReward() { return monetaryReward; }
    public void setMonetaryReward(BigDecimal monetaryReward) { this.monetaryReward = monetaryReward; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public Integer getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(Integer currentParticipants) { this.currentParticipants = currentParticipants; }

    public Boolean getRequiresEvidence() { return requiresEvidence; }
    public void setRequiresEvidence(Boolean requiresEvidence) { this.requiresEvidence = requiresEvidence; }

    public Boolean getAutoValidation() { return autoValidation; }
    public void setAutoValidation(Boolean autoValidation) { this.autoValidation = autoValidation; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
