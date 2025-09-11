package com.impulse.lean.domain.model;

import com.impulse.user.model.User;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * IMPULSE LEAN v1 - User Achievement Domain Model
 * 
 * Represents the relationship between users and their earned achievements
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Entity
@Table(name = "user_achievements", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "achievement_id", "earned_count"}))
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @CreationTimestamp
    @Column(name = "earned_at", nullable = false)
    private LocalDateTime earnedAt;

    @Column(name = "earned_count", nullable = false)
    private Integer earnedCount = 1;

    @Column(name = "progress_value")
    private Integer progressValue;

    @Column(name = "is_notified", nullable = false)
    private Boolean isNotified = false;

    @Column(name = "notification_sent_at")
    private LocalDateTime notificationSentAt;

    @Column(length = 1000)
    private String context;

    @Column(name = "source_type", length = 50)
    private String sourceType;

    @Column(name = "source_id")
    private Long sourceId;

    // Constructors
    public UserAchievement() {}

    public UserAchievement(User user, Achievement achievement) {
        this.user = user;
        this.achievement = achievement;
    }

    public UserAchievement(User user, Achievement achievement, String context) {
        this.user = user;
        this.achievement = achievement;
        this.context = context;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(LocalDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }

    public Integer getEarnedCount() {
        return earnedCount;
    }

    public void setEarnedCount(Integer earnedCount) {
        this.earnedCount = earnedCount;
    }

    public Integer getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(Integer progressValue) {
        this.progressValue = progressValue;
    }

    public Boolean getIsNotified() {
        return isNotified;
    }

    public void setIsNotified(Boolean isNotified) {
        this.isNotified = isNotified;
    }

    public LocalDateTime getNotificationSentAt() {
        return notificationSentAt;
    }

    public void setNotificationSentAt(LocalDateTime notificationSentAt) {
        this.notificationSentAt = notificationSentAt;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    // Business Methods
    public void incrementCount() {
        this.earnedCount++;
    }

    public boolean isRecentlyEarned() {
        return earnedAt != null && earnedAt.isAfter(LocalDateTime.now().minusHours(24));
    }

    public boolean needsNotification() {
        return !isNotified;
    }

    public void markAsNotified() {
        this.isNotified = true;
        this.notificationSentAt = LocalDateTime.now();
    }

    public String getDisplayText() {
        if (achievement == null) return "Unknown Achievement";
        
        String text = achievement.getTitle();
        if (earnedCount > 1) {
            text += " (" + earnedCount + "x)";
        }
        return text;
    }

    public Integer getTotalPoints() {
        if (achievement == null) return 0;
        return achievement.getPoints() * earnedCount;
    }

    @Override
    public String toString() {
        return "UserAchievement{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", achievementCode=" + (achievement != null ? achievement.getCode() : null) +
                ", earnedAt=" + earnedAt +
                ", earnedCount=" + earnedCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAchievement)) return false;
        
        UserAchievement that = (UserAchievement) o;
        
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        return achievement != null ? achievement.equals(that.achievement) : that.achievement == null;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (achievement != null ? achievement.hashCode() : 0);
        return result;
    }
}
