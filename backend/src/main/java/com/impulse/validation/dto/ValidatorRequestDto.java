package com.impulse.validation.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.impulse.lean.domain.model.CertificationLevel;
import com.impulse.lean.domain.model.ValidatorSpecialty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * IMPULSE LEAN v1 - Validator Request DTO
 * 
 * Data transfer object for validator registration and updates
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidatorRequestDto {

    @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
    private String bio;

    @Size(max = 2000, message = "Expertise description cannot exceed 2000 characters")
    private String expertise;

    private Set<ValidatorSpecialty> specialties;

    @Min(value = 1, message = "Maximum assignments must be at least 1")
    @Max(value = 50, message = "Maximum assignments cannot exceed 50")
    private Integer maxAssignments;

    private Boolean available;

    private CertificationLevel certificationLevel;

    // Constructors
    public ValidatorRequestDto() {}

    public ValidatorRequestDto(String bio, String expertise, Set<ValidatorSpecialty> specialties) {
        this.bio = bio;
        this.expertise = expertise;
        this.specialties = specialties;
    }

    // Validation methods
    public boolean hasValidSpecialties() {
        return specialties != null && !specialties.isEmpty();
    }

    public boolean isValidMaxAssignments() {
        return maxAssignments == null || (maxAssignments >= 1 && maxAssignments <= 50);
    }

    // Getters and Setters
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getExpertise() { return expertise; }
    public void setExpertise(String expertise) { this.expertise = expertise; }

    public Set<ValidatorSpecialty> getSpecialties() { return specialties; }
    public void setSpecialties(Set<ValidatorSpecialty> specialties) { this.specialties = specialties; }

    public Integer getMaxAssignments() { return maxAssignments; }
    public void setMaxAssignments(Integer maxAssignments) { this.maxAssignments = maxAssignments; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public CertificationLevel getCertificationLevel() { return certificationLevel; }
    public void setCertificationLevel(CertificationLevel certificationLevel) { this.certificationLevel = certificationLevel; }

    @Override
    public String toString() {
        return "ValidatorRequestDto{" +
                "bio='" + bio + '\'' +
                ", expertise='" + expertise + '\'' +
                ", specialties=" + specialties +
                ", maxAssignments=" + maxAssignments +
                ", available=" + available +
                ", certificationLevel=" + certificationLevel +
                '}';
    }
}
