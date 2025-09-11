package com.impulse.lean.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * IMPULSE LEAN v1 - Challenge List Response DTO
 * 
 * Data Transfer Object for challenge listing responses.
 * Includes comprehensive challenge information and metadata.
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public class ChallengeListResponseDto {

    private String uuid;
    private String title;
    private String description;
    private String category;
    private String difficulty;
    private String status;
    private String creatorName;
    private String creatorUuid;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Double completionRate;
    private Boolean isPublic;
    private Boolean isFeatured;
    private Boolean canJoin;
    private Boolean isParticipating;
    private String validationMethod;
    private List<String> tags;

    // Builder pattern for complex object construction
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ChallengeListResponseDto dto = new ChallengeListResponseDto();

        public Builder uuid(String uuid) {
            dto.uuid = uuid;
            return this;
        }

        public Builder title(String title) {
            dto.title = title;
            return this;
        }

        public Builder description(String description) {
            dto.description = description;
            return this;
        }

        public Builder category(String category) {
            dto.category = category;
            return this;
        }

        public Builder difficulty(String difficulty) {
            dto.difficulty = difficulty;
            return this;
        }

        public Builder status(String status) {
            dto.status = status;
            return this;
        }

        public Builder creatorName(String creatorName) {
            dto.creatorName = creatorName;
            return this;
        }

        public Builder creatorUuid(String creatorUuid) {
            dto.creatorUuid = creatorUuid;
            return this;
        }

        public Builder startDate(LocalDateTime startDate) {
            dto.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDateTime endDate) {
            dto.endDate = endDate;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            dto.createdAt = createdAt;
            return this;
        }

        public Builder maxParticipants(Integer maxParticipants) {
            dto.maxParticipants = maxParticipants;
            return this;
        }

        public Builder currentParticipants(Integer currentParticipants) {
            dto.currentParticipants = currentParticipants;
            return this;
        }

        public Builder completionRate(Double completionRate) {
            dto.completionRate = completionRate;
            return this;
        }

        public Builder isPublic(Boolean isPublic) {
            dto.isPublic = isPublic;
            return this;
        }

        public Builder isFeatured(Boolean isFeatured) {
            dto.isFeatured = isFeatured;
            return this;
        }

        public Builder canJoin(Boolean canJoin) {
            dto.canJoin = canJoin;
            return this;
        }

        public Builder isParticipating(Boolean isParticipating) {
            dto.isParticipating = isParticipating;
            return this;
        }

        public Builder validationMethod(String validationMethod) {
            dto.validationMethod = validationMethod;
            return this;
        }

        public Builder tags(List<String> tags) {
            dto.tags = tags;
            return this;
        }

        public ChallengeListResponseDto build() {
            return dto;
        }
    }

    // Helper methods for UI logic
    public boolean isActive() {
        return "ACTIVE".equals(status) || "PUBLISHED".equals(status);
    }

    public boolean isUpcoming() {
        return startDate != null && startDate.isAfter(LocalDateTime.now());
    }

    public boolean isExpired() {
        return endDate != null && endDate.isBefore(LocalDateTime.now());
    }

    public boolean isFull() {
        return maxParticipants != null && currentParticipants != null 
               && currentParticipants >= maxParticipants;
    }

    public int getAvailableSpots() {
        if (maxParticipants == null || currentParticipants == null) {
            return Integer.MAX_VALUE;
        }
        return Math.max(0, maxParticipants - currentParticipants);
    }

    // Getters and setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorUuid() {
        return creatorUuid;
    }

    public void setCreatorUuid(String creatorUuid) {
        this.creatorUuid = creatorUuid;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Integer getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public Double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(Double completionRate) {
        this.completionRate = completionRate;
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

    public Boolean getCanJoin() {
        return canJoin;
    }

    public void setCanJoin(Boolean canJoin) {
        this.canJoin = canJoin;
    }

    public Boolean getIsParticipating() {
        return isParticipating;
    }

    public void setIsParticipating(Boolean isParticipating) {
        this.isParticipating = isParticipating;
    }

    public String getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(String validationMethod) {
        this.validationMethod = validationMethod;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
