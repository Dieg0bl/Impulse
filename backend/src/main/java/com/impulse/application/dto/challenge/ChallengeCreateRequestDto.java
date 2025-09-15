package com.impulse.application.dto.challenge;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.impulse.domain.enums.ChallengeCategory;
import com.impulse.domain.enums.ChallengeDifficulty;
import com.impulse.domain.enums.ValidationType;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ChallengeCreateRequestDto {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres")
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 5000, message = "La descripción debe tener entre 10 y 5000 caracteres")
    private String description;

    @NotNull(message = "La categoría es obligatoria")
    private ChallengeCategory category;

    @NotNull(message = "La dificultad es obligatoria")
    private ChallengeDifficulty difficulty;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDateTime startDate;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDateTime endDate;

    @Min(value = 1, message = "La duración mínima es 1 día")
    @Max(value = 365, message = "La duración máxima es 365 días")
    private Integer durationDays;

    @Min(value = 1, message = "Debe permitir al menos 1 participante")
    @Max(value = 10000, message = "Máximo 10,000 participantes")
    private Integer maxParticipants;

    @DecimalMin(value = "0.0", message = "El precio mínimo es 0")
    @DecimalMax(value = "10000.0", message = "El precio máximo es 10,000")
    private BigDecimal price;

    @Size(max = 1000, message = "Las reglas no pueden exceder 1000 caracteres")
    private String rules;

    @Size(max = 2000, message = "Los criterios de evaluación no pueden exceder 2000 caracteres")
    private String evaluationCriteria;

    @NotNull(message = "El tipo de validación es obligatorio")
    private ValidationType validationType;

    private List<String> tags;

    private Boolean isPublic = true;

    private Boolean allowTeams = false;

    @Min(value = 1, message = "El tamaño mínimo del equipo es 1")
    @Max(value = 50, message = "El tamaño máximo del equipo es 50")
    private Integer maxTeamSize;

    // Constructors
    /**
     * Empty constructor required for frameworks and serialization.
     */
    public ChallengeCreateRequestDto() {}

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ChallengeCategory getCategory() {
        return category;
    }

    public void setCategory(ChallengeCategory category) {
        this.category = category;
    }

    public ChallengeDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(ChallengeDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getEvaluationCriteria() {
        return evaluationCriteria;
    }

    public void setEvaluationCriteria(String evaluationCriteria) {
        this.evaluationCriteria = evaluationCriteria;
    }

    public ValidationType getValidationType() {
        return validationType;
    }

    public void setValidationType(ValidationType validationType) {
        this.validationType = validationType;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getAllowTeams() {
        return allowTeams;
    }

    public void setAllowTeams(Boolean allowTeams) {
        this.allowTeams = allowTeams;
    }

    public Integer getMaxTeamSize() {
        return maxTeamSize;
    }

    public void setMaxTeamSize(Integer maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }
}
