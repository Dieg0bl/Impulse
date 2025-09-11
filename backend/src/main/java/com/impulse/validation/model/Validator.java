package com.impulse.validation.model;

import com.impulse.user.model.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * IMPULSE LEAN v1 - Validator Domain Model
 * 
 * Represents a validator who can validate evidence submissions
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Entity
@Table(name = "validators")
public class Validator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @UuidGenerator
    private String uuid;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ValidatorStatus status = ValidatorStatus.ACTIVE;

    @ElementCollection(targetClass = ValidatorSpecialty.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "validator_specialties", joinColumns = @JoinColumn(name = "validator_id"))
    @Column(name = "specialty")
    private Set<ValidatorSpecialty> specialties = new HashSet<>();

    @Column(name = "max_assignments", nullable = false)
    private Integer maxAssignments = 5;

    @Column(name = "current_assignments", nullable = false)
    private Integer currentAssignments = 0;

    @Column(name = "is_available", nullable = false)
    private Boolean available = true;

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.valueOf(5.0);

    @Column(name = "total_validations", nullable = false)
    private Integer totalValidations = 0;

    @Column(name = "successful_validations", nullable = false)
    private Integer successfulValidations = 0;

    @Column(name = "average_response_time", precision = 10, scale = 2)
    private BigDecimal averageResponseTimeHours;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "expertise", columnDefinition = "TEXT")
    private String expertise;

    @Column(name = "certification_level")
    @Enumerated(EnumType.STRING)
    private CertificationLevel certificationLevel = CertificationLevel.BASIC;

    @Column(name = "certified_at")
    private LocalDateTime certifiedAt;

    @Column(name = "certification_expires_at")
    private LocalDateTime certificationExpiresAt;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "validator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ValidatorAssignment> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "validator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<EvidenceValidation> validations = new ArrayList<>();

    // Constructors
    public Validator() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Validator(User user) {
        this();
        this.user = user;
        this.lastActivityAt = LocalDateTime.now();
    }

    // Business methods
    public boolean canAcceptAssignment() {
        return available && status == ValidatorStatus.ACTIVE && currentAssignments < maxAssignments;
    }

    public void addSpecialty(ValidatorSpecialty specialty) {
        specialties.add(specialty);
        updateTimestamp();
    }

    public void removeSpecialty(ValidatorSpecialty specialty) {
        specialties.remove(specialty);
        updateTimestamp();
    }

    public boolean hasSpecialty(ValidatorSpecialty specialty) {
        return specialties.contains(specialty);
    }

    public void incrementAssignments() {
        currentAssignments++;
        updateTimestamp();
    }

    public void decrementAssignments() {
        if (currentAssignments > 0) {
            currentAssignments--;
        }
        updateTimestamp();
    }

    public void updateValidationStats(boolean successful) {
        totalValidations++;
        if (successful) {
            successfulValidations++;
        }
        updateTimestamp();
    }

    public double getSuccessRate() {
        return totalValidations > 0 ? (double) successfulValidations / totalValidations : 0.0;
    }

    public boolean isOverloaded() {
        return currentAssignments >= maxAssignments;
    }

    public boolean isUnderutilized() {
        return currentAssignments == 0 && available;
    }

    public boolean isCertified() {
        return certifiedAt != null && 
               (certificationExpiresAt == null || certificationExpiresAt.isAfter(LocalDateTime.now()));
    }

    public boolean isCertificationExpired() {
        return certificationExpiresAt != null && certificationExpiresAt.isBefore(LocalDateTime.now());
    }

    public void updateActivity() {
        lastActivityAt = LocalDateTime.now();
        updateTimestamp();
    }

    public void updateRating(BigDecimal newRating) {
        this.rating = newRating;
        updateTimestamp();
    }

    public void certify(CertificationLevel level, LocalDateTime expiresAt) {
        this.certificationLevel = level;
        this.certifiedAt = LocalDateTime.now();
        this.certificationExpiresAt = expiresAt;
        updateTimestamp();
    }

    public void revokeCertification() {
        this.certificationLevel = CertificationLevel.BASIC;
        this.certifiedAt = null;
        this.certificationExpiresAt = null;
        updateTimestamp();
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTimestamp();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ValidatorStatus getStatus() { return status; }
    public void setStatus(ValidatorStatus status) { this.status = status; updateTimestamp(); }

    public Set<ValidatorSpecialty> getSpecialties() { return specialties; }
    public void setSpecialties(Set<ValidatorSpecialty> specialties) { this.specialties = specialties; }

    public Integer getMaxAssignments() { return maxAssignments; }
    public void setMaxAssignments(Integer maxAssignments) { this.maxAssignments = maxAssignments; updateTimestamp(); }

    public Integer getCurrentAssignments() { return currentAssignments; }
    public void setCurrentAssignments(Integer currentAssignments) { this.currentAssignments = currentAssignments; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; updateTimestamp(); }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public Integer getTotalValidations() { return totalValidations; }
    public void setTotalValidations(Integer totalValidations) { this.totalValidations = totalValidations; }

    public Integer getSuccessfulValidations() { return successfulValidations; }
    public void setSuccessfulValidations(Integer successfulValidations) { this.successfulValidations = successfulValidations; }

    public BigDecimal getAverageResponseTimeHours() { return averageResponseTimeHours; }
    public void setAverageResponseTimeHours(BigDecimal averageResponseTimeHours) { this.averageResponseTimeHours = averageResponseTimeHours; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; updateTimestamp(); }

    public String getExpertise() { return expertise; }
    public void setExpertise(String expertise) { this.expertise = expertise; updateTimestamp(); }

    public CertificationLevel getCertificationLevel() { return certificationLevel; }
    public void setCertificationLevel(CertificationLevel certificationLevel) { this.certificationLevel = certificationLevel; }

    public LocalDateTime getCertifiedAt() { return certifiedAt; }
    public void setCertifiedAt(LocalDateTime certifiedAt) { this.certifiedAt = certifiedAt; }

    public LocalDateTime getCertificationExpiresAt() { return certificationExpiresAt; }
    public void setCertificationExpiresAt(LocalDateTime certificationExpiresAt) { this.certificationExpiresAt = certificationExpiresAt; }

    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<ValidatorAssignment> getAssignments() { return assignments; }
    public void setAssignments(List<ValidatorAssignment> assignments) { this.assignments = assignments; }

    public List<EvidenceValidation> getValidations() { return validations; }
    public void setValidations(List<EvidenceValidation> validations) { this.validations = validations; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Validator)) return false;
        Validator validator = (Validator) o;
        return uuid != null && uuid.equals(validator.uuid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Validator{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", status=" + status +
                ", specialties=" + specialties +
                ", rating=" + rating +
                ", totalValidations=" + totalValidations +
                ", available=" + available +
                '}';
    }
}
