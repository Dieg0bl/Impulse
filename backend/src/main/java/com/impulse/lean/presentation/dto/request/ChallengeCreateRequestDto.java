package com.impulse.lean.presentation.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * IMPULSE LEAN v1 - Challenge Create Request DTO
 * 
 * Data Transfer Object for challenge creation requests.
 * Includes comprehensive validation rules for challenge data.
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public class ChallengeCreateRequestDto {

    @NotBlank(message = "Challenge title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Challenge description is required")
    @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters")
    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Difficulty is required")
    private String difficulty;

    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    @Positive(message = "Maximum participants must be positive")
    @Max(value = 10000, message = "Maximum participants cannot exceed 10,000")
    private Integer maxParticipants;

    @Size(max = 1000, message = "Requirements cannot exceed 1000 characters")
    private String requirements;

    @Size(max = 1000, message = "Guidelines cannot exceed 1000 characters")
    private String guidelines;

    private String validationMethod = "SELF_DECLARATION";

    private Boolean isPublic = true;

    private Boolean isFeatured = false;

    // Custom validation methods
    @AssertTrue(message = "End date must be after start date")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true; // Let other validators handle null checks
        }
        return endDate.isAfter(startDate);
    }

    @AssertTrue(message = "Challenge duration must be between 1 day and 1 year")
    public boolean isValidDuration() {
        if (startDate == null || endDate == null) {
            return true;
        }
        long daysBetween = java.time.Duration.between(startDate, endDate).toDays();
        return daysBetween >= 1 && daysBetween <= 365;
    }

    // Getters and setters
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
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

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getGuidelines() {
        return guidelines;
    }

    public void setGuidelines(String guidelines) {
        this.guidelines = guidelines;
    }

    public String getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(String validationMethod) {
        this.validationMethod = validationMethod;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
}
