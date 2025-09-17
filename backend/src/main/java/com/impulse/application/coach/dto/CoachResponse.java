package com.impulse.application.coach.dto;

import com.impulse.domain.enums.CoachStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CoachResponse - DTO de respuesta para Coach
 */
public class CoachResponse {
    private final Long id;
    private final String userId;
    private final String bio;
    private final String specializations;
    private final Integer yearsExperience;
    private final BigDecimal hourlyRate;
    private final CoachStatus status;
    private final String certificationUrl;
    private final BigDecimal averageRating;
    private final Integer totalRatings;
    private final Integer totalSessions;
    private final Boolean isVerified;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    public CoachResponse(Long id, String userId, String bio, String specializations, Integer yearsExperience, BigDecimal hourlyRate, CoachStatus status, String certificationUrl, BigDecimal averageRating, Integer totalRatings, Integer totalSessions, Boolean isVerified, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.bio = bio;
        this.specializations = specializations;
        this.yearsExperience = yearsExperience;
        this.hourlyRate = hourlyRate;
        this.status = status;
        this.certificationUrl = certificationUrl;
        this.averageRating = averageRating;
        this.totalRatings = totalRatings;
        this.totalSessions = totalSessions;
        this.isVerified = isVerified;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public String getBio() { return bio; }
    public String getSpecializations() { return specializations; }
    public Integer getYearsExperience() { return yearsExperience; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public CoachStatus getStatus() { return status; }
    public String getCertificationUrl() { return certificationUrl; }
    public BigDecimal getAverageRating() { return averageRating; }
    public Integer getTotalRatings() { return totalRatings; }
    public Integer getTotalSessions() { return totalSessions; }
    public Boolean getIsVerified() { return isVerified; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
