package com.impulse.lean.application.dto.challenge;

import java.time.LocalDateTime;
import java.util.List;

/**
 * IMPULSE LEAN v1 - Challenge List Response DTO
 * 
 * DTO for listing challenges with pagination support
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public class ChallengeListResponseDto {

    private String id;
    private String title;
    private String description;
    private String category;
    private String difficulty;
    private String status;
    private String visibility;
    private Integer participantCount;
    private Integer maxParticipants;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private String creatorName;
    private String creatorId;
    private List<String> tags;
    private String thumbnailUrl;
    private boolean featured;
    private boolean canJoin;
    private boolean hasJoined;
    private Double progressPercentage;

    // Default constructor required for JSON serialization
    public ChallengeListResponseDto() {
        // Empty constructor for framework compatibility
    }

    // Full constructor
    public ChallengeListResponseDto(String id, String title, String description, String category,
                                  String difficulty, String status, String visibility,
                                  Integer participantCount, Integer maxParticipants,
                                  LocalDateTime startDate, LocalDateTime endDate,
                                  LocalDateTime createdAt, String creatorName, String creatorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.status = status;
        this.visibility = visibility;
        this.participantCount = participantCount;
        this.maxParticipants = maxParticipants;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.creatorName = creatorName;
        this.creatorId = creatorId;
    }

    // Builder pattern for complex object creation
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ChallengeListResponseDto dto = new ChallengeListResponseDto();

        public Builder id(String id) { dto.id = id; return this; }
        public Builder title(String title) { dto.title = title; return this; }
        public Builder description(String description) { dto.description = description; return this; }
        public Builder category(String category) { dto.category = category; return this; }
        public Builder difficulty(String difficulty) { dto.difficulty = difficulty; return this; }
        public Builder status(String status) { dto.status = status; return this; }
        public Builder visibility(String visibility) { dto.visibility = visibility; return this; }
        public Builder participantCount(Integer count) { dto.participantCount = count; return this; }
        public Builder maxParticipants(Integer max) { dto.maxParticipants = max; return this; }
        public Builder startDate(LocalDateTime date) { dto.startDate = date; return this; }
        public Builder endDate(LocalDateTime date) { dto.endDate = date; return this; }
        public Builder createdAt(LocalDateTime date) { dto.createdAt = date; return this; }
        public Builder creatorName(String name) { dto.creatorName = name; return this; }
        public Builder creatorId(String id) { dto.creatorId = id; return this; }
        public Builder tags(List<String> tags) { dto.tags = tags; return this; }
        public Builder thumbnailUrl(String url) { dto.thumbnailUrl = url; return this; }
        public Builder featured(boolean featured) { dto.featured = featured; return this; }
        public Builder canJoin(boolean canJoin) { dto.canJoin = canJoin; return this; }
        public Builder hasJoined(boolean hasJoined) { dto.hasJoined = hasJoined; return this; }
        public Builder progressPercentage(Double progress) { dto.progressPercentage = progress; return this; }

        public ChallengeListResponseDto build() { return dto; }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public Integer getParticipantCount() { return participantCount; }
    public void setParticipantCount(Integer participantCount) { this.participantCount = participantCount; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public boolean isCanJoin() { return canJoin; }
    public void setCanJoin(boolean canJoin) { this.canJoin = canJoin; }

    public boolean isHasJoined() { return hasJoined; }
    public void setHasJoined(boolean hasJoined) { this.hasJoined = hasJoined; }

    public Double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Double progressPercentage) { this.progressPercentage = progressPercentage; }

    // Helper methods
    public boolean isFull() {
        return participantCount != null && maxParticipants != null && 
               participantCount >= maxParticipants;
    }

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public boolean hasStarted() {
        return startDate == null || LocalDateTime.now().isAfter(startDate);
    }

    public boolean hasEnded() {
        return endDate != null && LocalDateTime.now().isAfter(endDate);
    }

    @Override
    public String toString() {
        return "ChallengeListResponseDto{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", participantCount=" + participantCount +
                ", maxParticipants=" + maxParticipants +
                '}';
    }
}
