package com.impulse.lean.application.dto;

import com.impulse.lean.domain.model.ChallengeDifficulty;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Set;

public class CreateChallengeRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters")
    private String description;

    @NotBlank(message = "Objectives are required")
    @Size(min = 10, max = 1000, message = "Objectives must be between 10 and 1000 characters")
    private String objectives;

    @NotNull(message = "Difficulty level is required")
    private ChallengeDifficulty difficultyLevel;

    @NotNull(message = "Estimated duration is required")
    @Min(value = 1, message = "Estimated duration must be at least 1 day")
    @Max(value = 365, message = "Estimated duration cannot exceed 365 days")
    private Integer estimatedDuration;

    @NotEmpty(message = "At least one tag is required")
    @Size(max = 10, message = "Maximum 10 tags allowed")
    private Set<String> tags;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Min(value = 1, message = "Max participants must be at least 1")
    @Max(value = 10000, message = "Max participants cannot exceed 10000")
    private Integer maxParticipants;

    // Constructors
    public CreateChallengeRequest() {}

    public CreateChallengeRequest(String title, String description, String objectives, 
                                 ChallengeDifficulty difficultyLevel, Integer estimatedDuration, Set<String> tags) {
        this.title = title;
        this.description = description;
        this.objectives = objectives;
        this.difficultyLevel = difficultyLevel;
        this.estimatedDuration = estimatedDuration;
        this.tags = tags;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public ChallengeDifficulty getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(ChallengeDifficulty difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public Integer getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(Integer estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
}
