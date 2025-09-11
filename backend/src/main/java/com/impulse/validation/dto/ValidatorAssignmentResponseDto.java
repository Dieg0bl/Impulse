package com.impulse.validation.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.impulse.lean.domain.model.AssignmentStatus;
import com.impulse.lean.domain.model.ValidationPriority;
import com.impulse.lean.domain.model.ValidatorAssignment;

/**
 * IMPULSE LEAN v1 - Validator Assignment Response DTO
 * 
 * Data transfer object for validator assignment information in API responses
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidatorAssignmentResponseDto {

    private Long id;
    private String uuid;
    private String validatorUuid;
    private String validatorName;
    private String evidenceUuid;
    private String evidenceDescription;
    private String assignedByName;
    private AssignmentStatus status;
    private ValidationPriority priority;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime assignedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dueDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime acceptedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;

    private String notes;
    private String assignmentReason;
    private String completionNotes;
    private Boolean autoAssigned;
    private Boolean notificationSent;
    private Boolean reminderSent;
    private Boolean overdue;
    private Long durationHours;
    private Long timeToAcceptanceHours;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Constructors
    public ValidatorAssignmentResponseDto() {}

    public ValidatorAssignmentResponseDto(ValidatorAssignment assignment) {
        this.id = assignment.getId();
        this.uuid = assignment.getUuid();
        
        if (assignment.getValidator() != null) {
            this.validatorUuid = assignment.getValidator().getUuid();
            if (assignment.getValidator().getUser() != null) {
                this.validatorName = assignment.getValidator().getUser().getFullName();
            }
        }
        
        if (assignment.getEvidence() != null) {
            this.evidenceUuid = assignment.getEvidence().getUuid();
            this.evidenceDescription = assignment.getEvidence().getDescription();
        }
        
        if (assignment.getAssignedBy() != null) {
            this.assignedByName = assignment.getAssignedBy().getFullName();
        }
        
        this.status = assignment.getStatus();
        this.priority = assignment.getPriority();
        this.assignedAt = assignment.getAssignedAt();
        this.dueDate = assignment.getDueDate();
        this.acceptedAt = assignment.getAcceptedAt();
        this.startedAt = assignment.getStartedAt();
        this.completedAt = assignment.getCompletedAt();
        this.notes = assignment.getNotes();
        this.assignmentReason = assignment.getAssignmentReason();
        this.completionNotes = assignment.getCompletionNotes();
        this.autoAssigned = assignment.getAutoAssigned();
        this.notificationSent = assignment.getNotificationSent();
        this.reminderSent = assignment.getReminderSent();
        this.overdue = assignment.isOverdue();
        this.durationHours = assignment.getDurationInHours();
        this.timeToAcceptanceHours = assignment.getTimeToAcceptanceInHours();
        this.createdAt = assignment.getCreatedAt();
        this.updatedAt = assignment.getUpdatedAt();
    }

    // Static factory methods
    public static ValidatorAssignmentResponseDto fromEntity(ValidatorAssignment assignment) {
        return new ValidatorAssignmentResponseDto(assignment);
    }

    // Business methods
    public boolean isActive() {
        return status.isActive();
    }

    public boolean isCompleted() {
        return status.isCompleted();
    }

    public boolean isPendingAcceptance() {
        return status == AssignmentStatus.ASSIGNED;
    }

    public boolean hasHighPriority() {
        return priority != null && priority.isHighPriority();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getValidatorUuid() { return validatorUuid; }
    public void setValidatorUuid(String validatorUuid) { this.validatorUuid = validatorUuid; }

    public String getValidatorName() { return validatorName; }
    public void setValidatorName(String validatorName) { this.validatorName = validatorName; }

    public String getEvidenceUuid() { return evidenceUuid; }
    public void setEvidenceUuid(String evidenceUuid) { this.evidenceUuid = evidenceUuid; }

    public String getEvidenceDescription() { return evidenceDescription; }
    public void setEvidenceDescription(String evidenceDescription) { this.evidenceDescription = evidenceDescription; }

    public String getAssignedByName() { return assignedByName; }
    public void setAssignedByName(String assignedByName) { this.assignedByName = assignedByName; }

    public AssignmentStatus getStatus() { return status; }
    public void setStatus(AssignmentStatus status) { this.status = status; }

    public ValidationPriority getPriority() { return priority; }
    public void setPriority(ValidationPriority priority) { this.priority = priority; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getAssignmentReason() { return assignmentReason; }
    public void setAssignmentReason(String assignmentReason) { this.assignmentReason = assignmentReason; }

    public String getCompletionNotes() { return completionNotes; }
    public void setCompletionNotes(String completionNotes) { this.completionNotes = completionNotes; }

    public Boolean getAutoAssigned() { return autoAssigned; }
    public void setAutoAssigned(Boolean autoAssigned) { this.autoAssigned = autoAssigned; }

    public Boolean getNotificationSent() { return notificationSent; }
    public void setNotificationSent(Boolean notificationSent) { this.notificationSent = notificationSent; }

    public Boolean getReminderSent() { return reminderSent; }
    public void setReminderSent(Boolean reminderSent) { this.reminderSent = reminderSent; }

    public Boolean getOverdue() { return overdue; }
    public void setOverdue(Boolean overdue) { this.overdue = overdue; }

    public Long getDurationHours() { return durationHours; }
    public void setDurationHours(Long durationHours) { this.durationHours = durationHours; }

    public Long getTimeToAcceptanceHours() { return timeToAcceptanceHours; }
    public void setTimeToAcceptanceHours(Long timeToAcceptanceHours) { this.timeToAcceptanceHours = timeToAcceptanceHours; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "ValidatorAssignmentResponseDto{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", validatorName='" + validatorName + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", assignedAt=" + assignedAt +
                ", overdue=" + overdue +
                '}';
    }
}
