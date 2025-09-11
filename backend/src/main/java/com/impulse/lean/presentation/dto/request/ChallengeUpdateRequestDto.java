package com.impulse.lean.presentation.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * IMPULSE LEAN v1 - Challenge Update Request DTO
 * 
 * Data Transfer Object for challenge update requests.
 * All fields are optional to support partial updates.
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public class ChallengeUpdateRequestDto {

    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters")
    private String description;

    private String category;

    private String difficulty;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Positive(message = "Maximum participants must be positive")
    @Max(value = 10000, message = "Maximum participants cannot exceed 10,000")
    private Integer maxParticipants;

    @Size(max = 1000, message = "Requirements cannot exceed 1000 characters")
    private String requirements;

    @Size(max = 1000, message = "Guidelines cannot exceed 1000 characters")
    private String guidelines;

    private String validationMethod;

    private Boolean isPublic;

    private Boolean isFeatured;

    // Custom validation for date range if both dates are provided
    @AssertTrue(message = "End date must be after start date")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true; // Allow partial updates
        }
        return endDate.isAfter(startDate);
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
