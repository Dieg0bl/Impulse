package com.impulse.domain.coach;

import com.impulse.domain.enums.CoachStatus;
import com.impulse.domain.user.UserId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Coach - Entidad de dominio que representa a un coach del sistema
 *
 * SUPERVERSIÓN FUSIONADA que combina:
 * - Campos completos de la versión JPA (bio, specializations, hourlyRate, etc.)
 * - Value objects de la versión Clean Architecture (CoachId, UserId)
 * - Validaciones robustas y métodos de dominio
 * - Inmutabilidad con métodos de copia para cambios de estado
 * - Builder pattern para construcción flexible
 */
public class Coach {

    // Identificador único del coach
    private final CoachId id;

    // Referencia al usuario asociado
    private final UserId userId;

    // Información profesional
    private final String bio;
    private final String specializations;
    private final Integer yearsExperience;
    private final BigDecimal hourlyRate;
    private final String certificationUrl;

    // Estado y verificación
    private final CoachStatus status;
    private final boolean isVerified;

    // Métricas de rendimiento
    private final BigDecimal averageRating;
    private final Integer totalRatings;
    private final Integer totalSessions;

    // Fechas de control
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // Constructor completo privado
    private Coach(CoachId id, UserId userId, String bio, String specializations,
                 Integer yearsExperience, BigDecimal hourlyRate, String certificationUrl,
                 CoachStatus status, boolean isVerified, BigDecimal averageRating,
                 Integer totalRatings, Integer totalSessions,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id; // Puede ser null para nuevos coaches
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.bio = bio != null ? bio.trim() : null;
        this.specializations = specializations != null ? specializations.trim() : null;
        this.yearsExperience = validateYearsExperience(yearsExperience);
        this.hourlyRate = validateHourlyRate(hourlyRate);
        this.certificationUrl = certificationUrl != null ? certificationUrl.trim() : null;
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.isVerified = isVerified;
        this.averageRating = averageRating != null ? averageRating : BigDecimal.ZERO;
        this.totalRatings = totalRatings != null ? totalRatings : 0;
        this.totalSessions = totalSessions != null ? totalSessions : 0;
        this.createdAt = Objects.requireNonNull(createdAt, "Created date cannot be null");
        this.updatedAt = updatedAt;
    }

    // Factory method para crear nuevo coach
    public static Coach createNew(UserId userId, String bio, String specializations,
                                Integer yearsExperience, BigDecimal hourlyRate, String certificationUrl) {
        return new Builder()
            .withUserId(userId)
            .withBio(bio)
            .withSpecializations(specializations)
            .withYearsExperience(yearsExperience)
            .withHourlyRate(hourlyRate)
            .withCertificationUrl(certificationUrl)
            .withStatus(CoachStatus.PENDING_APPROVAL)
            .withIsVerified(false)
            .withAverageRating(BigDecimal.ZERO)
            .withTotalRatings(0)
            .withTotalSessions(0)
            .withCreatedAt(LocalDateTime.now())
            .build();
    }

    // Factory method para reconstruir desde persistencia
    public static Coach reconstruct(CoachId id, UserId userId, String bio, String specializations,
                                  Integer yearsExperience, BigDecimal hourlyRate, String certificationUrl,
                                  CoachStatus status, boolean isVerified, BigDecimal averageRating,
                                  Integer totalRatings, Integer totalSessions,
                                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Builder()
            .withId(id)
            .withUserId(userId)
            .withBio(bio)
            .withSpecializations(specializations)
            .withYearsExperience(yearsExperience)
            .withHourlyRate(hourlyRate)
            .withCertificationUrl(certificationUrl)
            .withStatus(status)
            .withIsVerified(isVerified)
            .withAverageRating(averageRating)
            .withTotalRatings(totalRatings)
            .withTotalSessions(totalSessions)
            .withCreatedAt(createdAt)
            .withUpdatedAt(updatedAt)
            .build();
    }

    // Métodos de dominio para gestión del estado (retornan nuevas instancias)
    public Coach verify() {
        return new Builder()
            .fromCoach(this)
            .withStatus(CoachStatus.ACTIVE)
            .withIsVerified(true)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public Coach suspend() {
        return new Builder()
            .fromCoach(this)
            .withStatus(CoachStatus.SUSPENDED)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public Coach activate() {
        if (!isVerified) {
            throw new CoachDomainError("Cannot activate unverified coach");
        }
        return new Builder()
            .fromCoach(this)
            .withStatus(CoachStatus.ACTIVE)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public Coach deactivate() {
        return new Builder()
            .fromCoach(this)
            .withStatus(CoachStatus.INACTIVE)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public Coach updateProfile(String bio, String specializations, BigDecimal hourlyRate) {
        return new Builder()
            .fromCoach(this)
            .withBio(bio)
            .withSpecializations(specializations)
            .withHourlyRate(hourlyRate)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public Coach updateCertification(String certificationUrl) {
        return new Builder()
            .fromCoach(this)
            .withCertificationUrl(certificationUrl)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public Coach updateExperience(Integer yearsExperience) {
        return new Builder()
            .fromCoach(this)
            .withYearsExperience(yearsExperience)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public Coach recordSession() {
        return new Builder()
            .fromCoach(this)
            .withTotalSessions(this.totalSessions + 1)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public Coach updateRating(BigDecimal newAverageRating, Integer newTotalRatings) {
        if (newAverageRating == null || newAverageRating.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoachDomainError("Invalid average rating: " + newAverageRating);
        }
        if (newTotalRatings == null || newTotalRatings < 0) {
            throw new CoachDomainError("Invalid total ratings: " + newTotalRatings);
        }

        return new Builder()
            .fromCoach(this)
            .withAverageRating(newAverageRating)
            .withTotalRatings(newTotalRatings)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    // Métodos de consulta del dominio
    public boolean isActive() {
        return status == CoachStatus.ACTIVE;
    }

    public boolean isPendingApproval() {
        return status == CoachStatus.PENDING_APPROVAL;
    }

    public boolean isSuspended() {
        return status == CoachStatus.SUSPENDED;
    }

    public boolean isInactive() {
        return status == CoachStatus.INACTIVE;
    }

    public boolean canAcceptSessions() {
        return isActive() && isVerified;
    }

    public boolean hasExperience() {
        return yearsExperience != null && yearsExperience > 0;
    }

    public boolean hasRatings() {
        return totalRatings != null && totalRatings > 0;
    }

    public boolean hasSessions() {
        return totalSessions != null && totalSessions > 0;
    }

    public boolean hasHourlyRate() {
        return hourlyRate != null && hourlyRate.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasCertification() {
        return certificationUrl != null && !certificationUrl.trim().isEmpty();
    }

    public boolean hasBio() {
        return bio != null && !bio.trim().isEmpty();
    }

    public boolean hasSpecializations() {
        return specializations != null && !specializations.trim().isEmpty();
    }

    public boolean isExperienced() {
        return hasExperience() && yearsExperience >= 3;
    }

    public boolean isHighlyRated() {
        return hasRatings() && averageRating.compareTo(new BigDecimal("4.0")) >= 0;
    }

    public boolean isTopPerformer() {
        return isHighlyRated() && hasSessions() && totalSessions >= 10;
    }

    // Validaciones privadas
    private Integer validateYearsExperience(Integer years) {
        if (years != null && years < 0) {
            throw new CoachDomainError("Years of experience cannot be negative: " + years);
        }
        return years;
    }

    private BigDecimal validateHourlyRate(BigDecimal rate) {
        if (rate != null && rate.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoachDomainError("Hourly rate cannot be negative: " + rate);
        }
        return rate;
    }

    // Getters
    public CoachId getId() { return id; }
    public UserId getUserId() { return userId; }
    public String getBio() { return bio; }
    public String getSpecializations() { return specializations; }
    public Integer getYearsExperience() { return yearsExperience; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public String getCertificationUrl() { return certificationUrl; }
    public CoachStatus getStatus() { return status; }
    public boolean getIsVerified() { return isVerified; }
    public BigDecimal getAverageRating() { return averageRating; }
    public Integer getTotalRatings() { return totalRatings; }
    public Integer getTotalSessions() { return totalSessions; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Métodos de compatibilidad para persistencia JPA
    public Long getIdValue() { return id != null ? id.asLong() : null; }
    public String getUserIdValue() { return userId.getValue(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coach coach = (Coach) o;
        return Objects.equals(id, coach.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Coach{" +
                "id=" + id +
                ", userId=" + userId +
                ", status=" + status +
                ", isVerified=" + isVerified +
                ", averageRating=" + averageRating +
                ", totalSessions=" + totalSessions +
                '}';
    }

    // Builder Pattern
    public static class Builder {
        private CoachId id;
        private UserId userId;
        private String bio;
        private String specializations;
        private Integer yearsExperience;
        private BigDecimal hourlyRate;
        private String certificationUrl;
        private CoachStatus status;
        private boolean isVerified;
        private BigDecimal averageRating;
        private Integer totalRatings;
        private Integer totalSessions;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder() {
            // Constructor vacío para inicialización del builder
        }

        public Builder fromCoach(Coach coach) {
            this.id = coach.id;
            this.userId = coach.userId;
            this.bio = coach.bio;
            this.specializations = coach.specializations;
            this.yearsExperience = coach.yearsExperience;
            this.hourlyRate = coach.hourlyRate;
            this.certificationUrl = coach.certificationUrl;
            this.status = coach.status;
            this.isVerified = coach.isVerified;
            this.averageRating = coach.averageRating;
            this.totalRatings = coach.totalRatings;
            this.totalSessions = coach.totalSessions;
            this.createdAt = coach.createdAt;
            this.updatedAt = coach.updatedAt;
            return this;
        }

        public Builder withId(CoachId id) {
            this.id = id;
            return this;
        }

        public Builder withUserId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public Builder withBio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder withSpecializations(String specializations) {
            this.specializations = specializations;
            return this;
        }

        public Builder withYearsExperience(Integer yearsExperience) {
            this.yearsExperience = yearsExperience;
            return this;
        }

        public Builder withHourlyRate(BigDecimal hourlyRate) {
            this.hourlyRate = hourlyRate;
            return this;
        }

        public Builder withCertificationUrl(String certificationUrl) {
            this.certificationUrl = certificationUrl;
            return this;
        }

        public Builder withStatus(CoachStatus status) {
            this.status = status;
            return this;
        }

        public Builder withIsVerified(boolean isVerified) {
            this.isVerified = isVerified;
            return this;
        }

        public Builder withAverageRating(BigDecimal averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public Builder withTotalRatings(Integer totalRatings) {
            this.totalRatings = totalRatings;
            return this;
        }

        public Builder withTotalSessions(Integer totalSessions) {
            this.totalSessions = totalSessions;
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

        public Coach build() {
            return new Coach(id, userId, bio, specializations, yearsExperience,
                           hourlyRate, certificationUrl, status, isVerified,
                           averageRating, totalRatings, totalSessions,
                           createdAt, updatedAt);
        }
    }
}
