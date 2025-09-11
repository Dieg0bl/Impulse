package com.impulse.validation.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.impulse.validation.model.ValidationPriority;

import jakarta.validation.constraints.Size;

/**
 * IMPULSE LEAN v1 - Validator Assignment Request DTO
 * 
 * Data transfer object for validator assignment requests
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidatorAssignmentRequestDto {

    private ValidationPriority priority;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dueDate;

    @Size(max = 1000, message = "Assignment reason cannot exceed 1000 characters")
    private String assignmentReason;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    private Boolean autoAssigned;

    private Boolean sendNotification;

    // Constructors
    public ValidatorAssignmentRequestDto() {}

    public ValidatorAssignmentRequestDto(ValidationPriority priority, LocalDateTime dueDate, String assignmentReason) {
        this.priority = priority;
        this.dueDate = dueDate;
        this.assignmentReason = assignmentReason;
        this.sendNotification = true; // Default to send notification
    }

    // Validation methods
    public boolean isValidDueDate() {
        return dueDate == null || dueDate.isAfter(LocalDateTime.now());
    }

    public boolean hasHighPriority() {
        return priority != null && priority.isHighPriority();
    }

    // Getters and Setters
    public ValidationPriority getPriority() { return priority; }
    public void setPriority(ValidationPriority priority) { this.priority = priority; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public String getAssignmentReason() { return assignmentReason; }
    public void setAssignmentReason(String assignmentReason) { this.assignmentReason = assignmentReason; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getAutoAssigned() { return autoAssigned; }
    public void setAutoAssigned(Boolean autoAssigned) { this.autoAssigned = autoAssigned; }

    public Boolean getSendNotification() { return sendNotification; }
    public void setSendNotification(Boolean sendNotification) { this.sendNotification = sendNotification; }

    @Override
    public String toString() {
        return "ValidatorAssignmentRequestDto{" +
                "priority=" + priority +
                ", dueDate=" + dueDate +
                ", assignmentReason='" + assignmentReason + '\'' +
                ", notes='" + notes + '\'' +
                ", autoAssigned=" + autoAssigned +
                ", sendNotification=" + sendNotification +
                '}';
    }
}
