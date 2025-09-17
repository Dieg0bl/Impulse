package com.impulse.domain.rating;

import com.impulse.domain.enums.RatingType;
import com.impulse.domain.user.UserId;
import com.impulse.domain.challenge.ChallengeId;
import com.impulse.domain.evidence.EvidenceId;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Rating - Entidad de dominio que representa una valoración del sistema
 *
 * SUPERVERSIÓN FUSIONADA que combina:
 * - Campos completos de la versión JPA (score, comment, type, isAnonymous, timestamps)
 * - Value objects de la versión Clean Architecture (RatingId, UserId)
 * - Validaciones de dominio y métodos de negocio
 * - Referencias tipadas a otras entidades (ChallengeId, EvidenceId, CoachId)
 */
public class Rating {

    // Identificador único de la valoración
    private final RatingId id;

    // Contenido de la valoración
    private final Integer score;
    private final String comment;
    private final RatingType type;
    private final boolean isAnonymous;

    // Fechas de control
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // Referencias a otras entidades (usando value objects)
    private final UserId raterUserId;
    private final ChallengeId challengeId;
    private final EvidenceId evidenceId;
    private final Long coachId; // Mantengo Long hasta migrar Coach a value object

    // Constructor completo
    private Rating(RatingId id, Integer score, String comment, RatingType type,
                  boolean isAnonymous, LocalDateTime createdAt, LocalDateTime updatedAt,
                  UserId raterUserId, ChallengeId challengeId, EvidenceId evidenceId, Long coachId) {
        this.id = Objects.requireNonNull(id, "Rating ID cannot be null");
        this.score = validateScore(score);
        this.comment = comment != null ? comment.trim() : null;
        this.type = Objects.requireNonNull(type, "Rating type cannot be null");
        this.isAnonymous = isAnonymous;
        this.createdAt = Objects.requireNonNull(createdAt, "Created date cannot be null");
        this.updatedAt = updatedAt;
        this.raterUserId = Objects.requireNonNull(raterUserId, "Rater user ID cannot be null");
        this.challengeId = challengeId;
        this.evidenceId = evidenceId;
        this.coachId = coachId;

        validateRatingTarget();
    }

    // Factory method para crear valoración de challenge
    public static Rating createChallengeRating(UserId raterUserId, ChallengeId challengeId,
                                             Integer score, String comment, boolean isAnonymous) {
        return new Builder()
            .withId(RatingId.generate())
            .withScore(score)
            .withComment(comment)
            .withType(RatingType.CHALLENGE)
            .withIsAnonymous(isAnonymous)
            .withCreatedAt(LocalDateTime.now())
            .withRaterUserId(raterUserId)
            .withChallengeId(challengeId)
            .build();
    }

    // Factory method para crear valoración de evidence
    public static Rating createEvidenceRating(UserId raterUserId, EvidenceId evidenceId,
                                            Integer score, String comment, boolean isAnonymous) {
        return new Builder()
            .withId(RatingId.generate())
            .withScore(score)
            .withComment(comment)
            .withType(RatingType.EVIDENCE)
            .withIsAnonymous(isAnonymous)
            .withCreatedAt(LocalDateTime.now())
            .withRaterUserId(raterUserId)
            .withEvidenceId(evidenceId)
            .build();
    }

    // Factory method para crear valoración de coach
    public static Rating createCoachRating(UserId raterUserId, Long coachId,
                                         Integer score, String comment, boolean isAnonymous) {
        return new Builder()
            .withId(RatingId.generate())
            .withScore(score)
            .withComment(comment)
            .withType(RatingType.COACH)
            .withIsAnonymous(isAnonymous)
            .withCreatedAt(LocalDateTime.now())
            .withRaterUserId(raterUserId)
            .withCoachId(coachId)
            .build();
    }

    // Factory method para reconstruir desde persistencia
    public static Rating reconstruct(RatingId id, Integer score, String comment, RatingType type,
                                   boolean isAnonymous, LocalDateTime createdAt, LocalDateTime updatedAt,
                                   UserId raterUserId, ChallengeId challengeId, EvidenceId evidenceId, Long coachId) {
        return new Builder()
            .withId(id)
            .withScore(score)
            .withComment(comment)
            .withType(type)
            .withIsAnonymous(isAnonymous)
            .withCreatedAt(createdAt)
            .withUpdatedAt(updatedAt)
            .withRaterUserId(raterUserId)
            .withChallengeId(challengeId)
            .withEvidenceId(evidenceId)
            .withCoachId(coachId)
            .build();
    }

    // Métodos de dominio
    public Rating updateComment(String newComment) {
        return new Builder()
            .fromRating(this)
            .withComment(newComment)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public Rating updateScore(Integer newScore) {
        return new Builder()
            .fromRating(this)
            .withScore(newScore)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public Rating makeAnonymous() {
        return new Builder()
            .fromRating(this)
            .withIsAnonymous(true)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public Rating makePublic() {
        return new Builder()
            .fromRating(this)
            .withIsAnonymous(false)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    // Métodos de consulta del dominio
    public boolean isPositive() {
        return score >= 4;
    }

    public boolean isNegative() {
        return score <= 2;
    }

    public boolean isNeutral() {
        return score == 3;
    }

    public boolean hasComment() {
        return comment != null && !comment.trim().isEmpty();
    }

    public boolean isChallengeRating() {
        return type == RatingType.CHALLENGE && challengeId != null;
    }

    public boolean isEvidenceRating() {
        return type == RatingType.EVIDENCE && evidenceId != null;
    }

    public boolean isCoachRating() {
        return type == RatingType.COACH && coachId != null;
    }

    public boolean isFromUser(UserId userId) {
        return this.raterUserId.equals(userId);
    }

    // Validaciones
    private Integer validateScore(Integer score) {
        Objects.requireNonNull(score, "Score cannot be null");
        if (score < 1 || score > 5) {
            throw new RatingDomainError("Score must be between 1 and 5, got: " + score);
        }
        return score;
    }

    private void validateRatingTarget() {
        long targetCount = 0;
        if (challengeId != null) targetCount++;
        if (evidenceId != null) targetCount++;
        if (coachId != null) targetCount++;

        if (targetCount != 1) {
            throw new RatingDomainError("Rating must have exactly one target (challenge, evidence, or coach)");
        }
    }

    // Getters
    public RatingId getId() { return id; }
    public Integer getScore() { return score; }
    public String getComment() { return comment; }
    public RatingType getType() { return type; }
    public boolean getIsAnonymous() { return isAnonymous; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public UserId getRaterUserId() { return raterUserId; }
    public ChallengeId getChallengeId() { return challengeId; }
    public EvidenceId getEvidenceId() { return evidenceId; }
    public Long getCoachId() { return coachId; }

    // Métodos de compatibilidad para persistencia JPA
    public Long getIdValue() { return id.getValue(); }
    public String getRaterUserIdValue() { return raterUserId.getValue(); }
    public String getChallengeIdValue() { return challengeId != null ? challengeId.getValue() : null; }
    public String getEvidenceIdValue() { return evidenceId != null ? evidenceId.getValue() : null; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rating rating = (Rating) o;
        return Objects.equals(id, rating.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Rating{" +
                "id=" + id +
                ", score=" + score +
                ", type=" + type +
                ", isAnonymous=" + isAnonymous +
                ", raterUserId=" + raterUserId +
                '}';
    }

    // Builder Pattern
    public static class Builder {
        private RatingId id;
        private Integer score;
        private String comment;
        private RatingType type;
        private boolean isAnonymous;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private UserId raterUserId;
        private ChallengeId challengeId;
        private EvidenceId evidenceId;
        private Long coachId;

        public Builder() {
            // Constructor vacío para inicialización del builder
        }

        public Builder fromRating(Rating rating) {
            this.id = rating.id;
            this.score = rating.score;
            this.comment = rating.comment;
            this.type = rating.type;
            this.isAnonymous = rating.isAnonymous;
            this.createdAt = rating.createdAt;
            this.updatedAt = rating.updatedAt;
            this.raterUserId = rating.raterUserId;
            this.challengeId = rating.challengeId;
            this.evidenceId = rating.evidenceId;
            this.coachId = rating.coachId;
            return this;
        }

        public Builder withId(RatingId id) {
            this.id = id;
            return this;
        }

        public Builder withScore(Integer score) {
            this.score = score;
            return this;
        }

        public Builder withComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder withType(RatingType type) {
            this.type = type;
            return this;
        }

        public Builder withIsAnonymous(boolean isAnonymous) {
            this.isAnonymous = isAnonymous;
            return this;
        }

        public Builder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder withRaterUserId(UserId raterUserId) {
            this.raterUserId = raterUserId;
            return this;
        }

        public Builder withChallengeId(ChallengeId challengeId) {
            this.challengeId = challengeId;
            return this;
        }

        public Builder withEvidenceId(EvidenceId evidenceId) {
            this.evidenceId = evidenceId;
            return this;
        }

        public Builder withCoachId(Long coachId) {
            this.coachId = coachId;
            return this;
        }

        public Rating build() {
            return new Rating(id, score, comment, type, isAnonymous, createdAt,
                            updatedAt, raterUserId, challengeId, evidenceId, coachId);
        }
    }
}
