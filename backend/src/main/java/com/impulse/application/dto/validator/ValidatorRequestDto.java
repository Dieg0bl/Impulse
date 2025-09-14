package com.impulse.application.dto.validator;

import com.impulse.domain.enums.ValidatorSpecialty;
import com.impulse.domain.model.CertificationLevel;
import jakarta.validation.constraints.*;

import java.util.List;

public class ValidatorRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String email;

    @Size(max = 1000, message = "La biografía no puede exceder 1000 caracteres")
    private String bio;

    @NotEmpty(message = "Debe seleccionar al menos una especialidad")
    private List<ValidatorSpecialty> specialties;

    @NotNull(message = "El nivel de certificación es obligatorio")
    private CertificationLevel certificationLevel;

    @Min(value = 0, message = "Los años de experiencia no pueden ser negativos")
    @Max(value = 50, message = "Los años de experiencia no pueden exceder 50")
    private Integer yearsOfExperience;

    @Size(max = 500, message = "Las certificaciones no pueden exceder 500 caracteres")
    private String certifications;

    @Size(max = 300, message = "Los idiomas no pueden exceder 300 caracteres")
    private String languages;

    private Boolean isAvailable = true;

    @DecimalMin(value = "0.0", message = "La tarifa no puede ser negativa")
    @DecimalMax(value = "10000.0", message = "La tarifa no puede exceder 10,000")
    private Double hourlyRate;

    @Size(max = 200, message = "La zona horaria no puede exceder 200 caracteres")
    private String timezone;

    @Min(value = 1, message = "La capacidad mínima es 1")
    @Max(value = 100, message = "La capacidad máxima es 100")
    private Integer maxCapacity;

    // Constructor por defecto
    public ValidatorRequestDto() {
        // Constructor vacío para serialización/deserialización
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<ValidatorSpecialty> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<ValidatorSpecialty> specialties) {
        this.specialties = specialties;
    }

    public CertificationLevel getCertificationLevel() {
        return certificationLevel;
    }

    public void setCertificationLevel(CertificationLevel certificationLevel) {
        this.certificationLevel = certificationLevel;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
