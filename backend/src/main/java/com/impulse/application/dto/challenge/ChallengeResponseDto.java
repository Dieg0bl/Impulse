package com.impulse.application.dto.challenge;

import com.impulse.domain.enums.ChallengeCategory;
import com.impulse.domain.enums.ChallengeStatus;
import com.impulse.domain.enums.ChallengeDifficulty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Challenge Response
 */
public class ChallengeResponseDto {
    private Long id;
    private String uuid;
    private String title;
    private String description;
    private ChallengeCategory category;
    private ChallengeDifficulty difficulty;
    private ChallengeStatus status;
    private BigDecimal rewardAmount;
    private String rewardCurrency;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long creatorId;
    private String creatorUsername;
    private Integer participantCount;
    private Integer maxParticipants;
    private Boolean isPublic;

    // Constructors
    public ChallengeResponseDto() {}

    public ChallengeResponseDto(Long id, String uuid, String title, String description,
                               ChallengeCategory category, ChallengeDifficulty difficulty,
                               ChallengeStatus status, BigDecimal rewardAmount, String rewardCurrency,
                               LocalDateTime startDate, LocalDateTime endDate,
                               LocalDateTime createdAt, LocalDateTime updatedAt,
                               Long creatorId, String creatorUsername,
                               Integer participantCount, Integer maxParticipants, Boolean isPublic) {
        this.id = id;
        this.uuid = uuid;
        this.title = title;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.status = status;
        this.rewardAmount = rewardAmount;
        this.rewardCurrency = rewardCurrency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.creatorId = creatorId;
        this.creatorUsername = creatorUsername;
        this.participantCount = participantCount;
        this.maxParticipants = maxParticipants;
        this.isPublic = isPublic;
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

    public BigDecimal getRewardAmount() { return rewardAmount; }
    public void setRewardAmount(BigDecimal rewardAmount) { this.rewardAmount = rewardAmount; }

    public String getRewardCurrency() { return rewardCurrency; }
    public void setRewardCurrency(String rewardCurrency) { this.rewardCurrency = rewardCurrency; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public String getCreatorUsername() { return creatorUsername; }
    public void setCreatorUsername(String creatorUsername) { this.creatorUsername = creatorUsername; }

    public Integer getParticipantCount() { return participantCount; }
    public void setParticipantCount(Integer participantCount) { this.participantCount = participantCount; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
}
