package com.impulse.challenge.model;

import com.impulse.user.model.User;
import com.impulse.validation.model.ValidationMethod;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Challenge-specific imports
import com.impulse.challenge.model.ChallengeCategory;
import com.impulse.challenge.model.ChallengeDifficulty;
import com.impulse.challenge.model.ChallengeStatus;
import com.impulse.challenge.model.ChallengeParticipation;

/**
 * IMPULSE LEAN v1 - Challenge Domain Entity
 * 
 * Core challenge entity with comprehensive metadata and state management
 * Supports gamification, validation workflows, and analytics
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Entity
@Table(name = "challenges",
       indexes = {
           @Index(name = "idx_challenges_creator_id", columnList = "creator_id"),
           @Index(name = "idx_challenges_slug", columnList = "slug"),
           @Index(name = "idx_challenges_category", columnList = "category"),
           @Index(name = "idx_challenges_difficulty", columnList = "difficulty"),
           @Index(name = "idx_challenges_status", columnList = "status"),
           @Index(name = "idx_challenges_start_date", columnList = "start_date"),
           @Index(name = "idx_challenges_featured", columnList = "featured")
       })
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, length = 36)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @NotBlank
    @Size(min = 5, max = 200)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank
    @Size(min = 3, max = 250)
    @Column(name = "slug", nullable = false, unique = true, length = 250)
    private String slug;

    @NotBlank
    @Size(min = 20, max = 5000)
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ChallengeCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private ChallengeDifficulty difficulty;

    @Min(1)
    @Max(365)
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Min(1)
    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Min(0)
    @Column(name = "reward_points", nullable = false)
    private Integer rewardPoints = 0;

    @Column(name = "evidence_required", nullable = false)
    private Boolean evidenceRequired = true;

    @Size(max = 1000)
    @Column(name = "evidence_description", columnDefinition = "TEXT")
    private String evidenceDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "validation_method", nullable = false)
    private ValidationMethod validationMethod = ValidationMethod.PEER;

    @Size(max = 2000)
    @Column(name = "validation_criteria", columnDefinition = "TEXT")
    private String validationCriteria;

    @Column(name = "tags", columnDefinition = "JSON")
    private String tags; // JSON array of strings

    @Size(max = 3000)
    @Column(name = "rules", columnDefinition = "TEXT")
    private String rules;

    @Column(name = "featured", nullable = false)
    private Boolean featured = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChallengeStatus status = ChallengeStatus.DRAFT;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChallengeParticipation> participations = new ArrayList<>();

    // Constructors
    public Challenge() {
        this.uuid = UUID.randomUUID().toString();
    }

    public Challenge(User creator, String title, String description, ChallengeCategory category, 
                    ChallengeDifficulty difficulty, Integer durationDays) {
        this();
        this.creator = creator;
        this.title = title;
        this.slug = generateSlugFromTitle(title);
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.durationDays = durationDays;
        this.rewardPoints = calculateDefaultRewardPoints();
    }

    // Business methods
    public boolean isActive() {
        return status == ChallengeStatus.PUBLISHED && 
               (startDate == null || startDate.isBefore(LocalDateTime.now())) &&
               (endDate == null || endDate.isAfter(LocalDateTime.now()));
    }

    public boolean canJoin() {
        return isActive() && 
               (maxParticipants == null || getActiveParticipantsCount() < maxParticipants);
    }

    public boolean isExpired() {
        return endDate != null && endDate.isBefore(LocalDateTime.now());
    }

    public boolean isStarted() {
        return startDate != null && startDate.isBefore(LocalDateTime.now());
    }

    public int getActiveParticipantsCount() {
        return (int) participations.stream()
                .filter(p -> p.getStatus() == ParticipationStatus.ACTIVE || 
                           p.getStatus() == ParticipationStatus.ENROLLED)
                .count();
    }

    public int getCompletedParticipantsCount() {
        return (int) participations.stream()
                .filter(p -> p.getStatus() == ParticipationStatus.COMPLETED)
                .count();
    }

    public double getCompletionRate() {
        long totalParticipants = participations.size();
        if (totalParticipants == 0) return 0.0;
        
        long completed = getCompletedParticipantsCount();
        return (double) completed / totalParticipants * 100.0;
    }

    public void publish() {
        if (status == ChallengeStatus.DRAFT) {
            status = ChallengeStatus.PUBLISHED;
            if (startDate == null) {
                startDate = LocalDateTime.now();
            }
        }
    }

    public void archive() {
        status = ChallengeStatus.ARCHIVED;
    }

    public void delete() {
        status = ChallengeStatus.DELETED;
    }

    public void feature() {
        featured = true;
    }

    public void unfeature() {
        featured = false;
    }

    private String generateSlugFromTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return UUID.randomUUID().toString();
        }
        
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "") // Remove special characters
                .replaceAll("\\s+", "-")         // Replace spaces with hyphens
                .replaceAll("-+", "-")           // Replace multiple hyphens with single
                .replaceAll("^-|-$", "")         // Remove leading/trailing hyphens
                + "-" + UUID.randomUUID().toString().substring(0, 8); // Add unique suffix
    }

    private Integer calculateDefaultRewardPoints() {
        int basePoints = 100;
        int difficultyMultiplier = difficulty.getPointsMultiplier();
        int durationBonus = Math.min(durationDays * 5, 200);
        return basePoints * difficultyMultiplier + durationBonus;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ChallengeCategory getCategory() { return category; }
    public void setCategory(ChallengeCategory category) { this.category = category; }

    public ChallengeDifficulty getDifficulty() { return difficulty; }
    public void setDifficulty(ChallengeDifficulty difficulty) { this.difficulty = difficulty; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public Integer getRewardPoints() { return rewardPoints; }
    public void setRewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; }

    public Boolean getEvidenceRequired() { return evidenceRequired; }
    public void setEvidenceRequired(Boolean evidenceRequired) { this.evidenceRequired = evidenceRequired; }

    public String getEvidenceDescription() { return evidenceDescription; }
    public void setEvidenceDescription(String evidenceDescription) { this.evidenceDescription = evidenceDescription; }

    public ValidationMethod getValidationMethod() { return validationMethod; }
    public void setValidationMethod(ValidationMethod validationMethod) { this.validationMethod = validationMethod; }

    public String getValidationCriteria() { return validationCriteria; }
    public void setValidationCriteria(String validationCriteria) { this.validationCriteria = validationCriteria; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }

    public Boolean getFeatured() { return featured; }
    public void setFeatured(Boolean featured) { this.featured = featured; }

    public ChallengeStatus getStatus() { return status; }
    public void setStatus(ChallengeStatus status) { this.status = status; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<ChallengeParticipation> getParticipations() { return participations; }
    public void setParticipations(List<ChallengeParticipation> participations) { this.participations = participations; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Challenge challenge = (Challenge) o;
        return Objects.equals(uuid, challenge.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", difficulty=" + difficulty +
                ", status=" + status +
                '}';
    }
}
