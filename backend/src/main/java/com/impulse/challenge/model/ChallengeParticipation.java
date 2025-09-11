package com.impulse.challenge.model;

import com.impulse.user.model.User;
import com.impulse.challenge.model.Challenge;
import com.impulse.lean.domain.model.ParticipationStatus;
import com.impulse.lean.domain.model.Evidence;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * IMPULSE LEAN v1 - Challenge Participation Domain Entity
 * 
 * Represents a user's participation in a specific challenge
 * Tracks progress, points earned, and completion status
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Entity
@Table(name = "challenge_participations",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_challenge_user", columnNames = {"challenge_id", "user_id"})
       },
       indexes = {
           @Index(name = "idx_participations_user_id", columnList = "user_id"),
           @Index(name = "idx_participations_status", columnList = "status"),
           @Index(name = "idx_participations_started_at", columnList = "started_at")
       })
public class ChallengeParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParticipationStatus status = ParticipationStatus.ENROLLED;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    @Column(name = "progress_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal progressPercentage = BigDecimal.ZERO;

    @Min(0)
    @Column(name = "current_day", nullable = false)
    private Integer currentDay = 0;

    @Min(0)
    @Column(name = "points_earned", nullable = false)
    private Integer pointsEarned = 0;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @Size(max = 2000)
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "participation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Evidence> evidences = new ArrayList<>();

    // Constructors
    public ChallengeParticipation() {}

    public ChallengeParticipation(Challenge challenge, User user) {
        this.challenge = challenge;
        this.user = user;
    }

    // Business methods
    public void start() {
        if (status == ParticipationStatus.ENROLLED) {
            status = ParticipationStatus.ACTIVE;
            startedAt = LocalDateTime.now();
            updateLastActivity();
        }
    }

    public void complete() {
        if (status == ParticipationStatus.ACTIVE) {
            status = ParticipationStatus.COMPLETED;
            completedAt = LocalDateTime.now();
            progressPercentage = new BigDecimal("100.00");
            currentDay = challenge.getDurationDays();
            updateLastActivity();
            calculateFinalPoints();
        }
    }

    public void fail() {
        if (status == ParticipationStatus.ACTIVE) {
            status = ParticipationStatus.FAILED;
            updateLastActivity();
        }
    }

    public void withdraw() {
        if (status.isActive()) {
            status = ParticipationStatus.WITHDRAWN;
            updateLastActivity();
        }
    }

    public void updateProgress(int day) {
        if (status == ParticipationStatus.ACTIVE && day <= challenge.getDurationDays()) {
            currentDay = Math.max(currentDay, day);
            
            // Calculate progress percentage
            progressPercentage = new BigDecimal(currentDay)
                .divide(new BigDecimal(challenge.getDurationDays()), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
            
            updateLastActivity();
            
            // Auto-complete if reached final day
            if (currentDay >= challenge.getDurationDays()) {
                complete();
            }
        }
    }

    public void updateLastActivity() {
        lastActivityAt = LocalDateTime.now();
    }

    public boolean isOnTrack() {
        if (status != ParticipationStatus.ACTIVE || startedAt == null) {
            return false;
        }
        
        long daysSinceStart = java.time.temporal.ChronoUnit.DAYS.between(
            startedAt.toLocalDate(), 
            LocalDateTime.now().toLocalDate()
        ) + 1;
        
        return currentDay >= daysSinceStart * 0.8; // Allow 20% tolerance
    }

    public boolean isOverdue() {
        if (status != ParticipationStatus.ACTIVE || startedAt == null) {
            return false;
        }
        
        long daysSinceStart = java.time.temporal.ChronoUnit.DAYS.between(
            startedAt.toLocalDate(), 
            LocalDateTime.now().toLocalDate()
        ) + 1;
        
        return daysSinceStart > challenge.getDurationDays();
    }

    public int getDaysRemaining() {
        if (status != ParticipationStatus.ACTIVE || startedAt == null) {
            return 0;
        }
        
        long daysSinceStart = java.time.temporal.ChronoUnit.DAYS.between(
            startedAt.toLocalDate(), 
            LocalDateTime.now().toLocalDate()
        ) + 1;
        
        return Math.max(0, challenge.getDurationDays() - (int) daysSinceStart);
    }

    public double getCompletionRate() {
        return progressPercentage.doubleValue();
    }

    public boolean hasEvidenceForDay(int day) {
        return evidences.stream()
                .anyMatch(e -> e.getDayNumber() == day);
    }

    public List<Evidence> getEvidencesForDay(int day) {
        return evidences.stream()
                .filter(e -> e.getDayNumber() == day)
                .toList();
    }

    private void calculateFinalPoints() {
        // Base points from challenge
        int basePoints = challenge.getRewardPoints();
        
        // Bonus for completion time
        double timeBonus = 1.0;
        if (startedAt != null && completedAt != null) {
            long actualDays = java.time.temporal.ChronoUnit.DAYS.between(
                startedAt.toLocalDate(), 
                completedAt.toLocalDate()
            ) + 1;
            
            if (actualDays <= challenge.getDurationDays()) {
                // Bonus for completing on time or early
                timeBonus = 1.0 + (0.1 * (challenge.getDurationDays() - actualDays) / challenge.getDurationDays());
            }
        }
        
        pointsEarned = (int) (basePoints * timeBonus);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Challenge getChallenge() { return challenge; }
    public void setChallenge(Challenge challenge) { this.challenge = challenge; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ParticipationStatus getStatus() { return status; }
    public void setStatus(ParticipationStatus status) { this.status = status; }

    public BigDecimal getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(BigDecimal progressPercentage) { this.progressPercentage = progressPercentage; }

    public Integer getCurrentDay() { return currentDay; }
    public void setCurrentDay(Integer currentDay) { this.currentDay = currentDay; }

    public Integer getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(Integer pointsEarned) { this.pointsEarned = pointsEarned; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Evidence> getEvidences() { return evidences; }
    public void setEvidences(List<Evidence> evidences) { this.evidences = evidences; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChallengeParticipation that = (ChallengeParticipation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ChallengeParticipation{" +
                "id=" + id +
                ", challengeId=" + (challenge != null ? challenge.getId() : null) +
                ", userId=" + (user != null ? user.getId() : null) +
                ", status=" + status +
                ", progress=" + progressPercentage +
                '}';
    }
}
