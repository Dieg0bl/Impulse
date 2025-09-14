package com.impulse.application.dto.challenge;

import com.impulse.domain.enums.ChallengeCategory;
import com.impulse.domain.enums.ChallengeDifficulty;
import com.impulse.domain.enums.ValidationType;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

public class ChallengeUpdateRequestDto {

    @Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres")
    private String title;

    @Size(min = 10, max = 5000, message = "La descripción debe tener entre 10 y 5000 caracteres")
    private String description;

    private ChallengeCategory category;

    private ChallengeDifficulty difficulty;

    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDateTime startDate;

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

    private ValidationType validationType;

    private List<String> tags;

    private Boolean isPublic;

    private Boolean allowTeams;

    @Min(value = 1, message = "El tamaño mínimo del equipo es 1")
    @Max(value = 50, message = "El tamaño máximo del equipo es 50")
    private Integer maxTeamSize;

    // Constructor por defecto
    public ChallengeUpdateRequestDto() {
        // Constructor vacío para serialización/deserialización
    }

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
