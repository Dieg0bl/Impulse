package com.impulse.adapters.http.evidence.dto;

import com.impulse.domain.enums.EvidenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * CreateEvidenceRequestDto - DTO de request para crear una evidencia
 */
public class CreateEvidenceRequestDto {

    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title cannot exceed 500 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Type is required")
    private EvidenceType type;

    @NotBlank(message = "Challenge ID is required")
    private String challengeId;

    private LocalDateTime validationDeadline;

    // Constructor vacío requerido por Jackson para deserialización
    public CreateEvidenceRequestDto() {
        // Constructor vacío para serialización/deserialización JSON
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public EvidenceType getType() { return type; }
    public void setType(EvidenceType type) { this.type = type; }

    public String getChallengeId() { return challengeId; }
    public void setChallengeId(String challengeId) { this.challengeId = challengeId; }

    public LocalDateTime getValidationDeadline() { return validationDeadline; }
    public void setValidationDeadline(LocalDateTime validationDeadline) { this.validationDeadline = validationDeadline; }
}
