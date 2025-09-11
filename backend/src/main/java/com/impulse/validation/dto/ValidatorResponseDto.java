package com.impulse.validation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.impulse.lean.domain.model.CertificationLevel;
import com.impulse.validation.model.Validator;
import com.impulse.validation.model.ValidatorSpecialty;
import com.impulse.validation.model.ValidatorStatus;

/**
 * IMPULSE LEAN v1 - Validator Response DTO
 * 
 * Data transfer object for validator information in API responses
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidatorResponseDto {

    private Long id;
    private String uuid;
    private String username;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private ValidatorStatus status;
    private Set<ValidatorSpecialty> specialties;
    private Integer maxAssignments;
    private Integer currentAssignments;
    private Boolean available;
    private BigDecimal rating;
    private Integer totalValidations;
    private Integer successfulValidations;
    private Double successRate;
    private BigDecimal averageResponseTimeHours;
    private String bio;
    private String expertise;
    private CertificationLevel certificationLevel;
    private Boolean certified;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime certifiedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime certificationExpiresAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastActivityAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Constructors
    public ValidatorResponseDto() {}

    public ValidatorResponseDto(Validator validator) {
        this.id = validator.getId();
        this.uuid = validator.getUuid();
        
        if (validator.getUser() != null) {
            this.username = validator.getUser().getUsername();
            this.firstName = validator.getUser().getFirstName();
            this.lastName = validator.getUser().getLastName();
            this.fullName = validator.getUser().getFullName();
            this.email = validator.getUser().getEmail();
        }
        
        this.status = validator.getStatus();
        this.specialties = validator.getSpecialties();
        this.maxAssignments = validator.getMaxAssignments();
        this.currentAssignments = validator.getCurrentAssignments();
        this.available = validator.getAvailable();
        this.rating = validator.getRating();
        this.totalValidations = validator.getTotalValidations();
        this.successfulValidations = validator.getSuccessfulValidations();
        this.successRate = validator.getSuccessRate();
        this.averageResponseTimeHours = validator.getAverageResponseTimeHours();
        this.bio = validator.getBio();
        this.expertise = validator.getExpertise();
        this.certificationLevel = validator.getCertificationLevel();
        this.certified = validator.isCertified();
        this.certifiedAt = validator.getCertifiedAt();
        this.certificationExpiresAt = validator.getCertificationExpiresAt();
        this.lastActivityAt = validator.getLastActivityAt();
        this.createdAt = validator.getCreatedAt();
        this.updatedAt = validator.getUpdatedAt();
    }

    // Static factory methods
    public static ValidatorResponseDto fromEntity(Validator validator) {
        return new ValidatorResponseDto(validator);
    }

    public static ValidatorResponseDto fromEntityWithoutSensitiveData(Validator validator) {
        ValidatorResponseDto dto = new ValidatorResponseDto(validator);
        dto.email = null; // Hide email for public endpoints
        return dto;
    }

    // Business methods
    public boolean isActive() {
        return status == ValidatorStatus.ACTIVE;
    }

    public boolean canAcceptAssignments() {
        return available && isActive() && currentAssignments < maxAssignments;
    }

    public boolean isOverloaded() {
        return currentAssignments >= maxAssignments;
    }

    public boolean hasHighRating() {
        return rating != null && rating.compareTo(BigDecimal.valueOf(4.5)) >= 0;
    }

    public boolean isExperienced() {
        return totalValidations != null && totalValidations >= 10;
    }

    public boolean hasSpecialty(ValidatorSpecialty specialty) {
        return specialties != null && specialties.contains(specialty);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public ValidatorStatus getStatus() { return status; }
    public void setStatus(ValidatorStatus status) { this.status = status; }

    public Set<ValidatorSpecialty> getSpecialties() { return specialties; }
    public void setSpecialties(Set<ValidatorSpecialty> specialties) { this.specialties = specialties; }

    public Integer getMaxAssignments() { return maxAssignments; }
    public void setMaxAssignments(Integer maxAssignments) { this.maxAssignments = maxAssignments; }

    public Integer getCurrentAssignments() { return currentAssignments; }
    public void setCurrentAssignments(Integer currentAssignments) { this.currentAssignments = currentAssignments; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public Integer getTotalValidations() { return totalValidations; }
    public void setTotalValidations(Integer totalValidations) { this.totalValidations = totalValidations; }

    public Integer getSuccessfulValidations() { return successfulValidations; }
    public void setSuccessfulValidations(Integer successfulValidations) { this.successfulValidations = successfulValidations; }

    public Double getSuccessRate() { return successRate; }
    public void setSuccessRate(Double successRate) { this.successRate = successRate; }

    public BigDecimal getAverageResponseTimeHours() { return averageResponseTimeHours; }
    public void setAverageResponseTimeHours(BigDecimal averageResponseTimeHours) { this.averageResponseTimeHours = averageResponseTimeHours; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getExpertise() { return expertise; }
    public void setExpertise(String expertise) { this.expertise = expertise; }

    public CertificationLevel getCertificationLevel() { return certificationLevel; }
    public void setCertificationLevel(CertificationLevel certificationLevel) { this.certificationLevel = certificationLevel; }

    public Boolean getCertified() { return certified; }
    public void setCertified(Boolean certified) { this.certified = certified; }

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

    @Override
    public String toString() {
        return "ValidatorResponseDto{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", status=" + status +
                ", specialties=" + specialties +
                ", available=" + available +
                ", rating=" + rating +
                '}';
    }
}
