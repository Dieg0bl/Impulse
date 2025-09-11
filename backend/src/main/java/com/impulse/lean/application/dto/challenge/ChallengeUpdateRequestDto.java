package com.impulse.lean.application.dto.challenge;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * IMPULSE LEAN v1 - Challenge Update Request DTO
 * 
 * DTO for updating existing challenges
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public class ChallengeUpdateRequestDto {

    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    private String title;

    @Size(min = 10, max = 10000, message = "Description must be between 10 and 10000 characters")
    private String description;

    private String category;
    private String difficulty;
    private LocalDateTime endDate; // Allow extending deadline
    private String visibility;

    @Min(value = 1, message = "At least 1 participant required")
    @Max(value = 1000, message = "Maximum 1000 participants allowed")
    private Integer maxParticipants;

    @Size(max = 1000, message = "Requirements too long")
    private String requirements;

    @Size(max = 1000, message = "Guidelines too long")
    private String guidelines;

    @Size(max = 5, message = "Maximum 5 tags allowed")
    private List<String> tags;

    // Constructors
    public ChallengeUpdateRequestDto() {}

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

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

    @Override
    public String toString() {
        return "ChallengeUpdateRequestDto{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", endDate=" + endDate +
                ", maxParticipants=" + maxParticipants +
                '}';
    }
}
