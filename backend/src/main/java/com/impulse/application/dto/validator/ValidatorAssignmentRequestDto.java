package com.impulse.application.dto.validator;

import com.impulse.domain.model.AssignmentStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class ValidatorAssignmentRequestDto {

    @NotNull(message = "El ID del validador es obligatorio")
    private Long validatorId;

    @NotNull(message = "El ID de la evidencia es obligatorio")
    private Long evidenceId;

    @NotNull(message = "El ID del reto es obligatorio")
    private Long challengeId;

    @NotNull(message = "La fecha límite es obligatoria")
    @Future(message = "La fecha límite debe ser futura")
    private LocalDateTime deadline;

    @Min(value = 1, message = "La prioridad mínima es 1")
    @Max(value = 10, message = "La prioridad máxima es 10")
    private Integer priority = 5;

    @Size(max = 500, message = "Las instrucciones especiales no pueden exceder 500 caracteres")
    private String specialInstructions;

    @NotNull(message = "El estado de asignación es obligatorio")
    private AssignmentStatus status = AssignmentStatus.PENDING;

    private Boolean autoAssign = false;

    private Boolean sendNotification = true;

    @DecimalMin(value = "0.0", message = "La compensación no puede ser negativa")
    @DecimalMax(value = "1000.0", message = "La compensación no puede exceder 1,000")
    private Double compensation;

    @Min(value = 1, message = "El tiempo estimado mínimo es 1 minuto")
    @Max(value = 480, message = "El tiempo estimado máximo es 480 minutos (8 horas)")
    private Integer estimatedTimeMinutes;

    // Constructor por defecto
    public ValidatorAssignmentRequestDto() {
        // Constructor vacío para serialización/deserialización
    }

    // Constructor con parámetros principales
    public ValidatorAssignmentRequestDto(Long validatorId, Long evidenceId, Long challengeId, LocalDateTime deadline) {
        this.validatorId = validatorId;
        this.evidenceId = evidenceId;
        this.challengeId = challengeId;
        this.deadline = deadline;
    }

    // Métodos de validación
    public boolean isHighPriority() {
        return priority != null && priority >= 8;
    }

    public boolean isUrgent() {
        return deadline != null && deadline.isBefore(LocalDateTime.now().plusHours(24));
    }

    public boolean hasCompensation() {
        return compensation != null && compensation > 0;
    }

    // Getters and Setters
    public Long getValidatorId() {
        return validatorId;
    }

    public void setValidatorId(Long validatorId) {
        this.validatorId = validatorId;
    }

    public Long getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(Long evidenceId) {
        this.evidenceId = evidenceId;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public Boolean getAutoAssign() {
        return autoAssign;
    }

    public void setAutoAssign(Boolean autoAssign) {
        this.autoAssign = autoAssign;
    }

    public Boolean getSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(Boolean sendNotification) {
        this.sendNotification = sendNotification;
    }

    public Double getCompensation() {
        return compensation;
    }

    public void setCompensation(Double compensation) {
        this.compensation = compensation;
    }

    public Integer getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(Integer estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }
}
