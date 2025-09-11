package com.impulse.lean.domain.model;

import com.impulse.user.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * IMPULSE LEAN v1 - User Statistics Domain Model
 * 
 * Tracks user performance metrics and gamification data
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Entity
@Table(name = "user_statistics")
public class UserStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Challenge Statistics
    @Column(name = "challenges_completed", nullable = false)
    private Integer challengesCompleted = 0;

    @Column(name = "challenges_participated", nullable = false)
    private Integer challengesParticipated = 0;

    @Column(name = "challenges_created", nullable = false)
    private Integer challengesCreated = 0;

    @Column(name = "challenges_won", nullable = false)
    private Integer challengesWon = 0;

    // Evidence Statistics
    @Column(name = "evidence_submitted", nullable = false)
    private Integer evidenceSubmitted = 0;

    @Column(name = "evidence_approved", nullable = false)
    private Integer evidenceApproved = 0;

    @Column(name = "evidence_rejected", nullable = false)
    private Integer evidenceRejected = 0;

    @Column(name = "average_evidence_score", precision = 5, scale = 2)
    private BigDecimal averageEvidenceScore = BigDecimal.ZERO;

    // Validation Statistics
    @Column(name = "validations_performed", nullable = false)
    private Integer validationsPerformed = 0;

    @Column(name = "validations_accuracy", precision = 5, scale = 2)
    private BigDecimal validationsAccuracy = BigDecimal.ZERO;

    @Column(name = "peer_validations_received", nullable = false)
    private Integer peerValidationsReceived = 0;

    // Points and Gamification
    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0;

    @Column(name = "current_level", nullable = false)
    private Integer currentLevel = 1;

    @Column(name = "experience_points", nullable = false)
    private Integer experiencePoints = 0;

    @Column(name = "achievements_earned", nullable = false)
    private Integer achievementsEarned = 0;

    // Activity and Engagement
    @Column(name = "current_streak", nullable = false)
    private Integer currentStreak = 0;

    @Column(name = "longest_streak", nullable = false)
    private Integer longestStreak = 0;

    @Column(name = "total_active_days", nullable = false)
    private Integer totalActiveDays = 0;

    @Column(name = "last_activity_date")
    private LocalDateTime lastActivityDate;

    // Profile and Social
    @Column(name = "profile_completion", nullable = false)
    private Integer profileCompletion = 0;

    @Column(name = "followers_count", nullable = false)
    private Integer followersCount = 0;

    @Column(name = "following_count", nullable = false)
    private Integer followingCount = 0;

    @Column(name = "reputation_score", precision = 8, scale = 2)
    private BigDecimal reputationScore = BigDecimal.ZERO;

    // Quality Metrics
    @Column(name = "quality_score", precision = 5, scale = 2)
    private BigDecimal qualityScore = BigDecimal.ZERO;

    @Column(name = "consistency_score", precision = 5, scale = 2)
    private BigDecimal consistencyScore = BigDecimal.ZERO;

    @Column(name = "collaboration_score", precision = 5, scale = 2)
    private BigDecimal collaborationScore = BigDecimal.ZERO;

    // Advanced Metrics
    @Column(name = "uci_score", precision = 8, scale = 4)
    private BigDecimal uciScore = BigDecimal.ZERO; // User Commitment Index

    @Column(name = "engagement_rate", precision = 5, scale = 2)
    private BigDecimal engagementRate = BigDecimal.ZERO;

    @Column(name = "response_time_avg", precision = 8, scale = 2)
    private BigDecimal averageResponseTime = BigDecimal.ZERO;

    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_calculated_at")
    private LocalDateTime lastCalculatedAt;

    // Constructors
    public UserStatistics() {}

    public UserStatistics(User user) {
        this.user = user;
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

    public Integer getChallengesCompleted() {
        return challengesCompleted;
    }

    public void setChallengesCompleted(Integer challengesCompleted) {
        this.challengesCompleted = challengesCompleted;
    }

    public Integer getChallengesParticipated() {
        return challengesParticipated;
    }

    public void setChallengesParticipated(Integer challengesParticipated) {
        this.challengesParticipated = challengesParticipated;
    }

    public Integer getChallengesCreated() {
        return challengesCreated;
    }

    public void setChallengesCreated(Integer challengesCreated) {
        this.challengesCreated = challengesCreated;
    }

    public Integer getChallengesWon() {
        return challengesWon;
    }

    public void setChallengesWon(Integer challengesWon) {
        this.challengesWon = challengesWon;
    }

    public Integer getEvidenceSubmitted() {
        return evidenceSubmitted;
    }

    public void setEvidenceSubmitted(Integer evidenceSubmitted) {
        this.evidenceSubmitted = evidenceSubmitted;
    }

    public Integer getEvidenceApproved() {
        return evidenceApproved;
    }

    public void setEvidenceApproved(Integer evidenceApproved) {
        this.evidenceApproved = evidenceApproved;
    }

    public Integer getEvidenceRejected() {
        return evidenceRejected;
    }

    public void setEvidenceRejected(Integer evidenceRejected) {
        this.evidenceRejected = evidenceRejected;
    }

    public BigDecimal getAverageEvidenceScore() {
        return averageEvidenceScore;
    }

    public void setAverageEvidenceScore(BigDecimal averageEvidenceScore) {
        this.averageEvidenceScore = averageEvidenceScore;
    }

    public Integer getValidationsPerformed() {
        return validationsPerformed;
    }

    public void setValidationsPerformed(Integer validationsPerformed) {
        this.validationsPerformed = validationsPerformed;
    }

    public BigDecimal getValidationsAccuracy() {
        return validationsAccuracy;
    }

    public void setValidationsAccuracy(BigDecimal validationsAccuracy) {
        this.validationsAccuracy = validationsAccuracy;
    }

    public Integer getPeerValidationsReceived() {
        return peerValidationsReceived;
    }

    public void setPeerValidationsReceived(Integer peerValidationsReceived) {
        this.peerValidationsReceived = peerValidationsReceived;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Integer getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Integer getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(Integer experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    public Integer getAchievementsEarned() {
        return achievementsEarned;
    }

    public void setAchievementsEarned(Integer achievementsEarned) {
        this.achievementsEarned = achievementsEarned;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    public Integer getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(Integer longestStreak) {
        this.longestStreak = longestStreak;
    }

    public Integer getTotalActiveDays() {
        return totalActiveDays;
    }

    public void setTotalActiveDays(Integer totalActiveDays) {
        this.totalActiveDays = totalActiveDays;
    }

    public LocalDateTime getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(LocalDateTime lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    public Integer getProfileCompletion() {
        return profileCompletion;
    }

    public void setProfileCompletion(Integer profileCompletion) {
        this.profileCompletion = profileCompletion;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public Integer getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }

    public BigDecimal getReputationScore() {
        return reputationScore;
    }

    public void setReputationScore(BigDecimal reputationScore) {
        this.reputationScore = reputationScore;
    }

    public BigDecimal getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(BigDecimal qualityScore) {
        this.qualityScore = qualityScore;
    }

    public BigDecimal getConsistencyScore() {
        return consistencyScore;
    }

    public void setConsistencyScore(BigDecimal consistencyScore) {
        this.consistencyScore = consistencyScore;
    }

    public BigDecimal getCollaborationScore() {
        return collaborationScore;
    }

    public void setCollaborationScore(BigDecimal collaborationScore) {
        this.collaborationScore = collaborationScore;
    }

    public BigDecimal getUciScore() {
        return uciScore;
    }

    public void setUciScore(BigDecimal uciScore) {
        this.uciScore = uciScore;
    }

    public BigDecimal getEngagementRate() {
        return engagementRate;
    }

    public void setEngagementRate(BigDecimal engagementRate) {
        this.engagementRate = engagementRate;
    }

    public BigDecimal getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(BigDecimal averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
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

    public LocalDateTime getLastCalculatedAt() {
        return lastCalculatedAt;
    }

    public void setLastCalculatedAt(LocalDateTime lastCalculatedAt) {
        this.lastCalculatedAt = lastCalculatedAt;
    }

    // Business Methods
    public BigDecimal getSuccessRate() {
        if (evidenceSubmitted == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(evidenceApproved)
                .divide(BigDecimal.valueOf(evidenceSubmitted), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal getChallengeCompletionRate() {
        if (challengesParticipated == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(challengesCompleted)
                .divide(BigDecimal.valueOf(challengesParticipated), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public Integer getNextLevelPoints() {
        return (currentLevel * 1000) + 500; // Example progression formula
    }

    public Integer getPointsToNextLevel() {
        return Math.max(0, getNextLevelPoints() - totalPoints);
    }

    public boolean isActive() {
        return lastActivityDate != null && 
               lastActivityDate.isAfter(LocalDateTime.now().minusDays(7));
    }

    public String getPerformanceLevel() {
        if (qualityScore.compareTo(BigDecimal.valueOf(90)) >= 0) return "Excellent";
        if (qualityScore.compareTo(BigDecimal.valueOf(75)) >= 0) return "Good";
        if (qualityScore.compareTo(BigDecimal.valueOf(50)) >= 0) return "Average";
        return "Needs Improvement";
    }

    // Additional methods for gamification service compatibility
    public Integer getLevelPoints() {
        return totalPoints % 1000; // Points in current level
    }

    public void setLevelPoints(Integer levelPoints) {
        // This is calculated, but setter needed for framework compatibility
    }

    public BigDecimal getSocialScore() {
        // Calculate social score based on social interactions
        if (this.likesGiven + this.commentsCount + this.sharesCount == 0) {
            return BigDecimal.ZERO;
        }
        
        int totalSocialActions = this.likesGiven + this.commentsCount + this.sharesCount + this.validationsPerformed;
        int positiveSocialActions = this.likesReceived + this.positiveValidationsReceived + this.sharesCount;
        
        return new BigDecimal(positiveSocialActions)
                .divide(new BigDecimal(totalSocialActions), 2, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    public Integer getTotalValidationsReceived() {
        return this.positiveValidationsReceived + this.negativeValidationsReceived;
    }

    public Integer getPositiveValidationsReceived() {
        return this.positiveValidationsReceived;
    }

    public Integer getTotalAchievements() {
        return this.totalAchievements;
    }

    public void setTotalAchievements(Integer totalAchievements) {
        this.totalAchievements = totalAchievements;
    }

    @Override
    public String toString() {
        return "UserStatistics{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", totalPoints=" + totalPoints +
                ", currentLevel=" + currentLevel +
                ", challengesCompleted=" + challengesCompleted +
                ", evidenceSubmitted=" + evidenceSubmitted +
                '}';
    }
}
