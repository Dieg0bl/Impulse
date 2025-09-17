package com.impulse.adapters.persistence.rating.entity;

import com.impulse.domain.enums.RatingType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
public class RatingJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer score;
    @Column(columnDefinition = "TEXT")
    private String comment;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RatingType type;
    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    private String raterUserId;
    private Long challengeId;
    private Long evidenceId;
    private Long coachId;
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public RatingType getType() { return type; }
    public void setType(RatingType type) { this.type = type; }
    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getRaterUserId() { return raterUserId; }
    public void setRaterUserId(String raterUserId) { this.raterUserId = raterUserId; }
    public Long getChallengeId() { return challengeId; }
    public void setChallengeId(Long challengeId) { this.challengeId = challengeId; }
    public Long getEvidenceId() { return evidenceId; }
    public void setEvidenceId(Long evidenceId) { this.evidenceId = evidenceId; }
    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }
}

