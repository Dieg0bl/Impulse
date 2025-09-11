package com.impulse.challenge.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.impulse.challenge.model.ChallengeCategory;
import com.impulse.challenge.model.ChallengeDifficulty;
import com.impulse.challenge.model.ChallengeStatus;

import java.time.LocalDateTime;

/**
 * IMPULSE LEAN v1 - Challenge Response DTO
 * 
 * Challenge data transfer object for API responses
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChallengeResponseDto {

    private Long id;
    private String uuid;
    private String title;
    private String description;
    private ChallengeCategory category;
    private ChallengeDifficulty difficulty;
    private ChallengeStatus status;
    private ValidationMethod validationMethod;
    private int maxParticipants;
    private long participantCount;
    private long completedCount;
    private String creatorName;
    private String creatorUuid;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // Constructors
    public ChallengeResponseDto() {}

    public ChallengeResponseDto(Challenge challenge) {
        this.id = challenge.getId();
        this.uuid = challenge.getUuid();
        this.title = challenge.getTitle();
        this.description = challenge.getDescription();
        this.category = challenge.getCategory();
        this.difficulty = challenge.getDifficulty();
        this.status = challenge.getStatus();
        this.validationMethod = challenge.getValidationMethod();
        this.maxParticipants = challenge.getMaxParticipants();
        this.startDate = challenge.getStartDate();
        this.endDate = challenge.getEndDate();
        this.createdAt = challenge.getCreatedAt();

        // Creator information
        if (challenge.getCreator() != null) {
            this.creatorName = challenge.getCreator().getFullName();
            this.creatorUuid = challenge.getCreator().getUuid();
        }

        // Calculate participation counts if available
        if (challenge.getParticipations() != null) {
            this.participantCount = challenge.getParticipations().size();
            this.completedCount = challenge.getParticipations().stream()
                .mapToLong(p -> p.getStatus() == ParticipationStatus.COMPLETED ? 1 : 0)
                .sum();
        }
    }

    // Static factory methods
    public static ChallengeResponseDto from(Challenge challenge) {
        return new ChallengeResponseDto(challenge);
    }

    public static ChallengeResponseDto summary(Challenge challenge) {
        ChallengeResponseDto dto = new ChallengeResponseDto();
        dto.id = challenge.getId();
        dto.uuid = challenge.getUuid();
        dto.title = challenge.getTitle();
        dto.category = challenge.getCategory();
        dto.difficulty = challenge.getDifficulty();
        dto.status = challenge.getStatus();
        dto.maxParticipants = challenge.getMaxParticipants();
        dto.startDate = challenge.getStartDate();
        dto.endDate = challenge.getEndDate();
        dto.createdAt = challenge.getCreatedAt();

        if (challenge.getCreator() != null) {
            dto.creatorName = challenge.getCreator().getFullName();
        }

        if (challenge.getParticipations() != null) {
            dto.participantCount = challenge.getParticipations().size();
        }

        return dto;
    }

    // Business methods
    private static final String UNKNOWN = "Unknown";
    
    public boolean isActive() {
        return status != null && status.name().equals("PUBLISHED");
    }

    public boolean hasStarted() {
        return startDate == null || LocalDateTime.now().isAfter(startDate);
    }

    public boolean hasEnded() {
        return endDate != null && LocalDateTime.now().isAfter(endDate);
    }

    public boolean isCurrentlyActive() {
        return isActive() && hasStarted() && !hasEnded();
    }

    public boolean isFull() {
        return participantCount >= maxParticipants;
    }

    public boolean canJoin() {
        return isCurrentlyActive() && !isFull();
    }

    public String getCategoryDisplayName() {
        return category != null ? category.getDisplayName() : UNKNOWN;
    }

    public String getDifficultyDisplayName() {
        return difficulty != null ? difficulty.getDisplayName() : UNKNOWN;
    }

    public String getStatusDisplayName() {
        return status != null ? status.getDisplayName() : UNKNOWN;
    }

    public double getCompletionRate() {
        if (participantCount == 0) return 0.0;
        return (double) completedCount / participantCount * 100;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ChallengeCategory getCategory() { return category; }
    public void setCategory(ChallengeCategory category) { this.category = category; }

    public ChallengeDifficulty getDifficulty() { return difficulty; }
    public void setDifficulty(ChallengeDifficulty difficulty) { this.difficulty = difficulty; }

    public ChallengeStatus getStatus() { return status; }
    public void setStatus(ChallengeStatus status) { this.status = status; }

    public ValidationMethod getValidationMethod() { return validationMethod; }
    public void setValidationMethod(ValidationMethod validationMethod) { this.validationMethod = validationMethod; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public long getParticipantCount() { return participantCount; }
    public void setParticipantCount(long participantCount) { this.participantCount = participantCount; }

    public long getCompletedCount() { return completedCount; }
    public void setCompletedCount(long completedCount) { this.completedCount = completedCount; }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public String getCreatorUuid() { return creatorUuid; }
    public void setCreatorUuid(String creatorUuid) { this.creatorUuid = creatorUuid; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
