package com.impulse.adapters.persistence.challenge.entity;

import com.impulse.domain.enums.ChallengeStatus;
import com.impulse.domain.enums.ChallengeType;
import com.impulse.domain.enums.DifficultyLevel;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ChallengeJpaEntity - Entidad JPA para Challenge
 */
@Entity
@Table(name = "challenges")
public class ChallengeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ChallengeType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private DifficultyLevel difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChallengeStatus status;

    @Column(name = "points_reward")
    private Integer pointsReward;

    @Column(name = "monetary_reward", precision = 10, scale = 2)
    private BigDecimal monetaryReward;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "current_participants", nullable = false)
    private Integer currentParticipants = 0;

    @Column(name = "requires_evidence", nullable = false)
    private Boolean requiresEvidence = false;

    @Column(name = "auto_validation", nullable = false)
    private Boolean autoValidation = false;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructor vacío requerido por JPA
    public ChallengeJpaEntity() {
        // Constructor vacío para JPA
    }

    // Constructor con parámetros principales
    public ChallengeJpaEntity(String title, String description, ChallengeType type,
                             DifficultyLevel difficulty, ChallengeStatus status, Long createdBy) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.difficulty = difficulty;
        this.status = status;
        this.createdBy = createdBy;
        this.currentParticipants = 0;
        this.requiresEvidence = false;
        this.autoValidation = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ChallengeType getType() { return type; }
    public void setType(ChallengeType type) { this.type = type; }

    public DifficultyLevel getDifficulty() { return difficulty; }
    public void setDifficulty(DifficultyLevel difficulty) { this.difficulty = difficulty; }

    public ChallengeStatus getStatus() { return status; }
    public void setStatus(ChallengeStatus status) { this.status = status; }

    public Integer getPointsReward() { return pointsReward; }
    public void setPointsReward(Integer pointsReward) { this.pointsReward = pointsReward; }

    public BigDecimal getMonetaryReward() { return monetaryReward; }
    public void setMonetaryReward(BigDecimal monetaryReward) { this.monetaryReward = monetaryReward; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public Integer getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(Integer currentParticipants) { this.currentParticipants = currentParticipants; }

    public Boolean getRequiresEvidence() { return requiresEvidence; }
    public void setRequiresEvidence(Boolean requiresEvidence) { this.requiresEvidence = requiresEvidence; }

    public Boolean getAutoValidation() { return autoValidation; }
    public void setAutoValidation(Boolean autoValidation) { this.autoValidation = autoValidation; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.currentParticipants == null) {
            this.currentParticipants = 0;
        }
        if (this.requiresEvidence == null) {
            this.requiresEvidence = false;
        }
        if (this.autoValidation == null) {
            this.autoValidation = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
