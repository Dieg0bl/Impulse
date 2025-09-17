package com.impulse.adapters.persistence.challengeparticipation.entity;

import com.impulse.domain.enums.ParticipationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "challenge_participations")
public class ChallengeParticipationJpaEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "challenge_id", nullable = false)
    private Long challengeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParticipationStatus status;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    @Column(name = "withdrawal_reason")
    private String withdrawalReason;

    @Column(name = "score")
    private Integer score;

    @Column(name = "progress_percentage")
    private Integer progressPercentage;

    // Constructors
    public ChallengeParticipationJpaEntity() {}

    public ChallengeParticipationJpaEntity(UUID id, String userId, Long challengeId,
                                          ParticipationStatus status, LocalDateTime joinedAt,
                                          LocalDateTime completedAt, LocalDateTime withdrawnAt,
                                          String withdrawalReason, Integer score,
                                          Integer progressPercentage) {
        this.id = id;
        this.userId = userId;
        this.challengeId = challengeId;
        this.status = status;
        this.joinedAt = joinedAt;
        this.completedAt = completedAt;
        this.withdrawnAt = withdrawnAt;
        this.withdrawalReason = withdrawalReason;
        this.score = score;
        this.progressPercentage = progressPercentage;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getChallengeId() { return challengeId; }
    public void setChallengeId(Long challengeId) { this.challengeId = challengeId; }

    public ParticipationStatus getStatus() { return status; }
    public void setStatus(ParticipationStatus status) { this.status = status; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getWithdrawnAt() { return withdrawnAt; }
    public void setWithdrawnAt(LocalDateTime withdrawnAt) { this.withdrawnAt = withdrawnAt; }

    public String getWithdrawalReason() { return withdrawalReason; }
    public void setWithdrawalReason(String withdrawalReason) { this.withdrawalReason = withdrawalReason; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }
}
