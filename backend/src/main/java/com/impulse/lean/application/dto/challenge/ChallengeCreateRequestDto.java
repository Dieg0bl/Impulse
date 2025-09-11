package com.impulse.lean.application.dto.challenge;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * IMPULSE LEAN v1 - Challenge Creation Request DTO
 * 
 * DTO for creating new challenges with validation rules
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public class ChallengeCreateRequestDto {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 10000, message = "Description must be between 10 and 10000 characters")
    private String description;

    @NotNull(message = "Category is required")
    private String category;

    @NotNull(message = "Difficulty is required")
    private String difficulty;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @NotNull(message = "Visibility is required")
    private String visibility; // private, validators, public

    @Min(value = 1, message = "At least 1 participant required")
    @Max(value = 1000, message = "Maximum 1000 participants allowed")
    private Integer maxParticipants = 10;

    @Size(max = 1000, message = "Requirements too long")
    private String requirements;

    @Size(max = 1000, message = "Guidelines too long")
    private String guidelines;

    @Size(max = 5, message = "Maximum 5 tags allowed")
    private List<String> tags;

    @Size(min = 1, max = 10, message = "At least 1 validator required, maximum 10")
    private List<String> validatorEmails;

    // Constructors
    public ChallengeCreateRequestDto() {}

    public ChallengeCreateRequestDto(String title, String description, String category, 
                                   String difficulty, LocalDateTime startDate, LocalDateTime endDate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public String getGuidelines() { return guidelines; }
    public void setGuidelines(String guidelines) { this.guidelines = guidelines; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public List<String> getValidatorEmails() { return validatorEmails; }
    public void setValidatorEmails(List<String> validatorEmails) { this.validatorEmails = validatorEmails; }

    @AssertTrue(message = "End date must be after start date")
    public boolean isEndDateAfterStartDate() {
        if (startDate == null || endDate == null) {
            return true; // Let @NotNull handle null validation
        }
        return endDate.isAfter(startDate);
    }

    @AssertTrue(message = "Challenge duration cannot exceed 365 days")
    public boolean isValidDuration() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return startDate.plusDays(365).isAfter(endDate);
    }

    @Override
    public String toString() {
        return "ChallengeCreateRequestDto{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", maxParticipants=" + maxParticipants +
                '}';
    }
}
