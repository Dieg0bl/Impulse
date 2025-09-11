package com.impulse.lean.domain.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * IMPULSE LEAN v1 - Achievement Domain Model
 * 
 * Represents user achievements and badges in the gamification system
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementLevel level;

    @Column(nullable = false)
    private Integer points;

    @Column(length = 500)
    private String iconUrl;

    @Column(length = 500)
    private String badgeUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementStatus status = AchievementStatus.ACTIVE;

    // Criteria for earning the achievement
    @Column(length = 100)
    private String criteriaType;

    @Column
    private Integer criteriaValue;

    @Column(length = 1000)
    private String criteriaDescription;

    // Metadata
    @Column
    private Boolean isRepeatable = false;

    @Column
    private Integer maxRepeats;

    @Column
    private Boolean isSecret = false;

    @Column
    private Integer sortOrder = 0;

    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    // Constructors
    public Achievement() {}

    public Achievement(String code, String title, AchievementCategory category, 
                      AchievementType type, AchievementLevel level, Integer points) {
        this.code = code;
        this.title = title;
        this.category = category;
        this.type = type;
        this.level = level;
        this.points = points;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public AchievementCategory getCategory() {
        return category;
    }

    public void setCategory(AchievementCategory category) {
        this.category = category;
    }

    public AchievementType getType() {
        return type;
    }

    public void setType(AchievementType type) {
        this.type = type;
    }

    public AchievementLevel getLevel() {
        return level;
    }

    public void setLevel(AchievementLevel level) {
        this.level = level;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getBadgeUrl() {
        return badgeUrl;
    }

    public void setBadgeUrl(String badgeUrl) {
        this.badgeUrl = badgeUrl;
    }

    public AchievementStatus getStatus() {
        return status;
    }

    public void setStatus(AchievementStatus status) {
        this.status = status;
    }

    public String getCriteriaType() {
        return criteriaType;
    }

    public void setCriteriaType(String criteriaType) {
        this.criteriaType = criteriaType;
    }

    public Integer getCriteriaValue() {
        return criteriaValue;
    }

    public void setCriteriaValue(Integer criteriaValue) {
        this.criteriaValue = criteriaValue;
    }

    public String getCriteriaDescription() {
        return criteriaDescription;
    }

    public void setCriteriaDescription(String criteriaDescription) {
        this.criteriaDescription = criteriaDescription;
    }

    public Boolean getIsRepeatable() {
        return isRepeatable;
    }

    public void setIsRepeatable(Boolean isRepeatable) {
        this.isRepeatable = isRepeatable;
    }

    public Integer getMaxRepeats() {
        return maxRepeats;
    }

    public void setMaxRepeats(Integer maxRepeats) {
        this.maxRepeats = maxRepeats;
    }

    public Boolean getIsSecret() {
        return isSecret;
    }

    public void setIsSecret(Boolean isSecret) {
        this.isSecret = isSecret;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    // Business methods
    public boolean canBeEarnedBy(UserStatistics stats) {
        if (this.status != AchievementStatus.ACTIVE || !this.isActive) {
            return false;
        }

        if (this.criteriaType == null || this.criteriaValue == null) {
            return false;
        }

        switch (this.criteriaType) {
            case "CHALLENGES_COMPLETED":
                return stats.getChallengesCompleted() >= this.criteriaValue;
            case "EVIDENCE_SUBMITTED":
                return stats.getEvidenceSubmitted() >= this.criteriaValue;
            case "VALIDATIONS_PERFORMED":
                return stats.getValidationsPerformed() >= this.criteriaValue;
            case "POINTS_EARNED":
                return stats.getTotalPoints() >= this.criteriaValue;
            case "STREAK_DAYS":
                return stats.getCurrentStreak() >= this.criteriaValue;
            case "PROFILE_COMPLETION":
                return stats.getProfileCompletion() >= this.criteriaValue;
            case "LEVEL_REACHED":
                return stats.getCurrentLevel() >= this.criteriaValue;
            default:
                return false;
        }
    }

    public boolean canBeEarnedByUser(User user) {
        // Alternative method that takes User and gets UserStatistics
        // This would require UserStatisticsService injection, 
        // so for now we'll use a simplified check
        return this.status == AchievementStatus.ACTIVE && this.isActive;
    }

    public String getDisplayTitle() {
        return this.title + (this.isSecret ? " (Hidden)" : "");
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", level=" + level +
                ", points=" + points +
                '}';
    }
}
