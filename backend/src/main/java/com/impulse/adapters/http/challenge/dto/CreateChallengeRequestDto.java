package com.impulse.adapters.http.challenge.dto;

import com.impulse.domain.enums.ChallengeType;
import com.impulse.domain.enums.DifficultyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CreateChallengeRequestDto - DTO de request para crear un challenge
 */
public class CreateChallengeRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Type is required")
    private ChallengeType type;

    @NotNull(message = "Difficulty is required")
    private DifficultyLevel difficulty;

    @PositiveOrZero(message = "Points reward must be zero or positive")
    private Integer pointsReward;

    @PositiveOrZero(message = "Monetary reward must be zero or positive")
    private BigDecimal monetaryReward;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Positive(message = "Max participants must be positive")
    private Integer maxParticipants;

    private Boolean requiresEvidence = false;
    private Boolean autoValidation = false;

    // Constructor vacío requerido por Jackson para deserialización
    public CreateChallengeRequestDto() {
        // Constructor vacío para serialización/deserialización JSON
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ChallengeType getType() { return type; }
    public void setType(ChallengeType type) { this.type = type; }

    public DifficultyLevel getDifficulty() { return difficulty; }
    public void setDifficulty(DifficultyLevel difficulty) { this.difficulty = difficulty; }

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

    public Boolean getRequiresEvidence() { return requiresEvidence; }
    public void setRequiresEvidence(Boolean requiresEvidence) { this.requiresEvidence = requiresEvidence; }

    public Boolean getAutoValidation() { return autoValidation; }
    public void setAutoValidation(Boolean autoValidation) { this.autoValidation = autoValidation; }
}
