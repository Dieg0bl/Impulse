package com.impulse.domain.challenge;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Challenge domain entity following DDD principles with JPA annotations
 */
@Entity
@Table(name = "challenges", indexes = {
    @Index(name = "idx_challenges_status", columnList = "status"),
    @Index(name = "idx_challenges_type", columnList = "challenge_type"),
    @Index(name = "idx_challenges_created", columnList = "created_at"),
    @Index(name = "idx_challenges_creator", columnList = "creator_id")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Challenge {
    
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "challenge_type", nullable = false, length = 50)
    private String challengeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ChallengeStatus status = ChallengeStatus.ACTIVE;

    @Column(name = "reward_amount", precision = 10, scale = 2)
    private Double rewardAmount;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "creator_id", nullable = false, columnDefinition = "CHAR(36)")
    private String creatorId;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructor for new challenges
    public Challenge(String title, String description, String challengeType, 
                    String creatorId, Double rewardAmount, Integer maxParticipants) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.challengeType = challengeType;
        this.creatorId = creatorId;
        this.rewardAmount = rewardAmount;
        this.maxParticipants = maxParticipants;
        this.status = ChallengeStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods
    public boolean isActive() {
        return this.status == ChallengeStatus.ACTIVE && 
               (this.expiresAt == null || this.expiresAt.isAfter(LocalDateTime.now()));
    }

    public boolean canAcceptParticipants() {
        return isActive() && 
               (this.maxParticipants == null || getCurrentParticipants() < this.maxParticipants);
    }

    public void complete() {
        this.status = ChallengeStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        this.status = ChallengeStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = ChallengeStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return this.expiresAt != null && this.expiresAt.isBefore(LocalDateTime.now());
    }

    public void setExpirationDate(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
        this.updatedAt = LocalDateTime.now();
    }

    // This would be calculated by counting related entities
    private int getCurrentParticipants() {
        // TODO: Implement participant counting logic
        return 0;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
