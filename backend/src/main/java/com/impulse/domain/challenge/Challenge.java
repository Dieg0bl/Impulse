package com.impulse.domain.challenge;

import com.impulse.domain.enums.ChallengeStatus;
import com.impulse.domain.enums.ChallengeType;
import com.impulse.domain.enums.DifficultyLevel;
import com.impulse.domain.user.UserId;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Challenge - Entidad de dominio pura (sin anotaciones de framework)
 * Representa un desafío en el sistema Impulse
 * SUPERVERSIÓN FUSIONADA con toda la funcionalidad completa
 */
public class Challenge {

    private final ChallengeId id;
    private String title;
    private String description;
    private final ChallengeType type;
    private ChallengeStatus status;
    private final DifficultyLevel difficulty;
    private Integer pointsReward;
    private BigDecimal monetaryReward;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Boolean requiresEvidence;
    private Boolean autoValidation;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final UserId createdBy;

    // Constructor completo - FUSIONADO
    public Challenge(ChallengeId id, String title, String description, ChallengeType type,
                    DifficultyLevel difficulty, UserId createdBy, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id, "ChallengeId cannot be null");
        this.title = validateAndSetTitle(title);
        this.description = description;
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.difficulty = Objects.requireNonNull(difficulty, "Difficulty cannot be null");
        this.createdBy = Objects.requireNonNull(createdBy, "Creator cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created date cannot be null");

        // Estados iniciales mejorados
        this.status = ChallengeStatus.DRAFT;
        this.currentParticipants = 0;
        this.requiresEvidence = true;
        this.autoValidation = false;
        this.updatedAt = createdAt;
    }

    // Constructor con parámetros completos - BUILDER PATTERN
    private Challenge(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "ChallengeId cannot be null");
        this.title = validateAndSetTitle(builder.title);
        this.description = builder.description;
        this.type = Objects.requireNonNull(builder.type, "Type cannot be null");
        this.status = builder.status != null ? builder.status : ChallengeStatus.DRAFT;
        this.difficulty = Objects.requireNonNull(builder.difficulty, "Difficulty cannot be null");
        this.pointsReward = builder.pointsReward;
        this.monetaryReward = builder.monetaryReward;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.maxParticipants = builder.maxParticipants;
        this.currentParticipants = builder.currentParticipants != null ? builder.currentParticipants : 0;
        this.requiresEvidence = builder.requiresEvidence != null ? builder.requiresEvidence : Boolean.TRUE;
        this.autoValidation = builder.autoValidation != null ? builder.autoValidation : Boolean.FALSE;
        this.createdAt = Objects.requireNonNull(builder.createdAt, "Created date cannot be null");
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : builder.createdAt;
        this.createdBy = Objects.requireNonNull(builder.createdBy, "Creator cannot be null");
    }

    // Builder class
    public static class Builder {
        private ChallengeId id;
        private String title;
        private String description;
        private ChallengeType type;
        private ChallengeStatus status;
        private DifficultyLevel difficulty;
        private Integer pointsReward;
        private BigDecimal monetaryReward;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer maxParticipants;
        private Integer currentParticipants;
        private Boolean requiresEvidence;
        private Boolean autoValidation;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private UserId createdBy;

        public Builder id(ChallengeId id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder type(ChallengeType type) { this.type = type; return this; }
        public Builder status(ChallengeStatus status) { this.status = status; return this; }
        public Builder difficulty(DifficultyLevel difficulty) { this.difficulty = difficulty; return this; }
        public Builder pointsReward(Integer pointsReward) { this.pointsReward = pointsReward; return this; }
        public Builder monetaryReward(BigDecimal monetaryReward) { this.monetaryReward = monetaryReward; return this; }
        public Builder startDate(LocalDateTime startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDateTime endDate) { this.endDate = endDate; return this; }
        public Builder maxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; return this; }
        public Builder currentParticipants(Integer currentParticipants) { this.currentParticipants = currentParticipants; return this; }
        public Builder requiresEvidence(Boolean requiresEvidence) { this.requiresEvidence = requiresEvidence; return this; }
        public Builder autoValidation(Boolean autoValidation) { this.autoValidation = autoValidation; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder createdBy(UserId createdBy) { this.createdBy = createdBy; return this; }

        public Challenge build() { return new Challenge(this); }
    }

    public static Builder builder() { return new Builder(); }

    // Factory method para crear nuevo challenge
    public static Challenge create(String title, String description, ChallengeType type,
                                 DifficultyLevel difficulty, UserId createdBy) {
        return new Challenge(
            ChallengeId.generate(),
            title,
            description,
            type,
            difficulty,
            createdBy,
            LocalDateTime.now()
        );
    }

    // Factory method con recompensas
    public static Challenge createWithRewards(String title, String description, ChallengeType type,
                                            DifficultyLevel difficulty, UserId createdBy,
                                            Integer pointsReward, BigDecimal monetaryReward) {
        Challenge challenge = create(title, description, type, difficulty, createdBy);
        challenge.setRewards(pointsReward, monetaryReward);
        return challenge;
    }

    // Métodos de dominio - MEJORADOS
    public void activate() {
        validateActivation();
        this.status = ChallengeStatus.ACTIVE;
        this.updateTimestamp();
    }

    public void complete() {
        if (this.status != ChallengeStatus.ACTIVE) {
            throw new ChallengeDomainError("Can only complete active challenges");
        }
        this.status = ChallengeStatus.COMPLETED;
        this.updateTimestamp();
    }

    public void cancel() {
        if (this.status == ChallengeStatus.COMPLETED) {
            throw new ChallengeDomainError("Cannot cancel completed challenge");
        }
        if (this.status == ChallengeStatus.DELETED) {
            throw new ChallengeDomainError("Cannot cancel deleted challenge");
        }
        this.status = ChallengeStatus.CANCELLED;
        this.updateTimestamp();
    }

    public void delete() {
        if (this.status == ChallengeStatus.ACTIVE && this.currentParticipants > 0) {
            throw new ChallengeDomainError("Cannot delete active challenge with participants");
        }
        this.status = ChallengeStatus.DELETED;
        this.updateTimestamp();
    }

    public void addParticipant() {
        if (this.status != ChallengeStatus.ACTIVE) {
            throw new ChallengeDomainError("Challenge must be active to add participants");
        }
        if (this.maxParticipants != null && this.currentParticipants >= this.maxParticipants) {
            throw new ChallengeDomainError("Challenge has reached maximum participants");
        }
        if (hasExpired()) {
            throw new ChallengeDomainError("Cannot join expired challenge");
        }
        this.currentParticipants++;
        this.updateTimestamp();
    }

    public void removeParticipant() {
        if (this.currentParticipants > 0) {
            this.currentParticipants--;
            this.updateTimestamp();
        }
    }

    public void updateTitle(String newTitle) {
        this.title = validateAndSetTitle(newTitle);
        this.updateTimestamp();
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
        this.updateTimestamp();
    }

    public void setRewards(Integer points, BigDecimal monetary) {
        this.pointsReward = points;
        this.monetaryReward = monetary;
        this.updateTimestamp();
    }

    public void setDates(LocalDateTime start, LocalDateTime end) {
        validateDates(start, end);
        this.startDate = start;
        this.endDate = end;
        this.updateTimestamp();
    }

    public void setMaxParticipants(Integer max) {
        if (max != null && max < this.currentParticipants) {
            throw new ChallengeDomainError("Max participants cannot be less than current participants");
        }
        this.maxParticipants = max;
        this.updateTimestamp();
    }

    // Métodos de consulta de dominio - AMPLIADOS
    public boolean isActive() {
        return this.status == ChallengeStatus.ACTIVE;
    }

    public boolean isDraft() {
        return this.status == ChallengeStatus.DRAFT;
    }

    public boolean isCompleted() {
        return this.status == ChallengeStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return this.status == ChallengeStatus.CANCELLED;
    }

    public boolean isDeleted() {
        return this.status == ChallengeStatus.DELETED;
    }

    public boolean canAcceptParticipants() {
        return isActive() && !hasExpired() &&
               (maxParticipants == null || currentParticipants < maxParticipants);
    }

    public boolean hasExpired() {
        return this.endDate != null && LocalDateTime.now().isAfter(this.endDate);
    }

    public boolean hasStarted() {
        return this.startDate != null && LocalDateTime.now().isAfter(this.startDate);
    }

    public boolean hasRewards() {
        return (pointsReward != null && pointsReward > 0) ||
               (monetaryReward != null && monetaryReward.compareTo(BigDecimal.ZERO) > 0);
    }

    public boolean isFull() {
        return maxParticipants != null && currentParticipants >= maxParticipants;
    }

    // Métodos de validación privados
    private String validateAndSetTitle(String title) {
        Objects.requireNonNull(title, "Title cannot be null");
        if (title.trim().isEmpty()) {
            throw new ChallengeDomainError("Title cannot be empty");
        }
        if (title.length() > 200) {
            throw new ChallengeDomainError("Title cannot exceed 200 characters");
        }
        return title.trim();
    }

    private void validateActivation() {
        if (this.status == ChallengeStatus.DELETED) {
            throw new ChallengeDomainError("Cannot activate deleted challenge");
        }
        if (this.status == ChallengeStatus.COMPLETED) {
            throw new ChallengeDomainError("Cannot activate completed challenge");
        }
        if (this.startDate == null || this.endDate == null) {
            throw new ChallengeDomainError("Cannot activate challenge without dates");
        }
        if (hasExpired()) {
            throw new ChallengeDomainError("Cannot activate expired challenge");
        }
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new ChallengeDomainError("Start date cannot be after end date");
        }
        if (end != null && end.isBefore(LocalDateTime.now())) {
            throw new ChallengeDomainError("End date cannot be in the past");
        }
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters completos
    public ChallengeId getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ChallengeType getType() { return type; }
    public ChallengeStatus getStatus() { return status; }
    public DifficultyLevel getDifficulty() { return difficulty; }
    public Integer getPointsReward() { return pointsReward; }
    public BigDecimal getMonetaryReward() { return monetaryReward; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public Integer getCurrentParticipants() { return currentParticipants; }
    public Boolean getRequiresEvidence() { return requiresEvidence; }
    public Boolean getAutoValidation() { return autoValidation; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public UserId getCreatedBy() { return createdBy; }

    // Setters para propiedades mutables específicas
    public void setRequiresEvidence(Boolean requiresEvidence) {
        this.requiresEvidence = requiresEvidence != null ? requiresEvidence : Boolean.TRUE;
        this.updateTimestamp();
    }

    public void setAutoValidation(Boolean autoValidation) {
        this.autoValidation = autoValidation != null ? autoValidation : Boolean.FALSE;
        this.updateTimestamp();
    }

    // Método de compatibilidad temporal
    public String getUuid() {
        return id != null ? id.getValue() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Challenge challenge = (Challenge) o;
        return Objects.equals(id, challenge.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", difficulty=" + difficulty +
                ", participants=" + currentParticipants +
                (maxParticipants != null ? "/" + maxParticipants : "") +
                '}';
    }
}
