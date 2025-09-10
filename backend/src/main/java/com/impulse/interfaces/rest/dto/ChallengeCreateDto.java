package com.impulse.interfaces.rest.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO para creación de challenges
 */
public class ChallengeCreateDto {
    
    @NotBlank(message = "Título es requerido")
    @Size(min = 5, max = 200, message = "Título debe tener entre 5 y 200 caracteres")
    private String title;
    
    @NotBlank(message = "Descripción es requerida")
    @Size(min = 20, max = 5000, message = "Descripción debe tener entre 20 y 5000 caracteres")
    private String description;
    
    @NotBlank(message = "Categoría es requerida")
    private String category;
    
    @NotBlank(message = "Dificultad es requerida")
    private String difficulty;
    
    @NotNull(message = "Puntos son requeridos")
    @Min(value = 1, message = "Puntos deben ser al menos 1")
    @Max(value = 10000, message = "Puntos no pueden exceder 10000")
    private Integer points;
    
    @Min(value = 1, message = "Duración estimada debe ser al menos 1 minuto")
    private Integer estimatedDurationMinutes;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    @Size(max = 1000, message = "Instrucciones no pueden exceder 1000 caracteres")
    private String instructions;
    
    @Size(max = 1000, message = "Criterios de éxito no pueden exceder 1000 caracteres")
    private String successCriteria;
    
    private Set<String> tags;
    private Set<String> requiredSkills;
    private Boolean isPublic = true;
    private Boolean allowTeams = false;
    
    @Min(value = 1, message = "Máximo de participantes debe ser al menos 1")
    private Integer maxParticipants;
    
    // Constructors
    public ChallengeCreateDto() {}
    
    public ChallengeCreateDto(String title, String description, String category, String difficulty, Integer points) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.points = points;
    }
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    
    public Integer getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }
    
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    
    public String getSuccessCriteria() { return successCriteria; }
    public void setSuccessCriteria(String successCriteria) { this.successCriteria = successCriteria; }
    
    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
    
    public Set<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(Set<String> requiredSkills) { this.requiredSkills = requiredSkills; }
    
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    
    public Boolean getAllowTeams() { return allowTeams; }
    public void setAllowTeams(Boolean allowTeams) { this.allowTeams = allowTeams; }
    
    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }
}
