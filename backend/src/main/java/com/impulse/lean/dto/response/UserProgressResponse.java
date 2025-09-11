package com.impulse.lean.dto.response;

import java.math.BigDecimal;

/**
 * IMPULSE LEAN v1 - User Progress Response DTO
 * 
 * Response DTO for user progress data
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public class UserProgressResponse {

    private Integer currentLevel;
    private Integer levelPoints;
    private Integer pointsToNextLevel;
    private BigDecimal uciScore;
    private Integer totalAchievements;
    private Long totalPoints;
    private Integer currentStreak;
    private Integer longestStreak;
    private BigDecimal successRate;

    // Constructors
    public UserProgressResponse() {}

    public UserProgressResponse(Integer currentLevel, Integer levelPoints, Integer pointsToNextLevel,
                               BigDecimal uciScore, Integer totalAchievements, Long totalPoints,
                               Integer currentStreak, Integer longestStreak, BigDecimal successRate) {
        this.currentLevel = currentLevel;
        this.levelPoints = levelPoints;
        this.pointsToNextLevel = pointsToNextLevel;
        this.uciScore = uciScore;
        this.totalAchievements = totalAchievements;
        this.totalPoints = totalPoints;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.successRate = successRate;
    }

    // Getters and Setters
    public Integer getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Integer getLevelPoints() {
        return levelPoints;
    }

    public void setLevelPoints(Integer levelPoints) {
        this.levelPoints = levelPoints;
    }

    public Integer getPointsToNextLevel() {
        return pointsToNextLevel;
    }

    public void setPointsToNextLevel(Integer pointsToNextLevel) {
        this.pointsToNextLevel = pointsToNextLevel;
    }

    public BigDecimal getUciScore() {
        return uciScore;
    }

    public void setUciScore(BigDecimal uciScore) {
        this.uciScore = uciScore;
    }

    public Integer getTotalAchievements() {
        return totalAchievements;
    }

    public void setTotalAchievements(Integer totalAchievements) {
        this.totalAchievements = totalAchievements;
    }

    public Long getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Long totalPoints) {
        this.totalPoints = totalPoints;
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

    public BigDecimal getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(BigDecimal successRate) {
        this.successRate = successRate;
    }
}
