package com.impulse.lean.domain.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import com.impulse.user.model.User;

/**
 * IMPULSE LEAN v1 - Validator Assignment Domain Model
 * 
 * Represents an assignment of a validator to validate evidence
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Entity
@Table(name = "validator_assignments")
public class ValidatorAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @UuidGenerator
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validator_id", nullable = false)
    private Validator validator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_id", nullable = false)
    private Evidence evidence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", nullable = false)
    private User assignedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssignmentStatus status = AssignmentStatus.ASSIGNED;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private ValidationPriority priority = ValidationPriority.NORMAL;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "assignment_reason", columnDefinition = "TEXT")
    private String assignmentReason;

    @Column(name = "completion_notes", columnDefinition = "TEXT")
    private String completionNotes;

    @Column(name = "auto_assigned", nullable = false)
    private Boolean autoAssigned = false;

    @Column(name = "notification_sent", nullable = false)
    private Boolean notificationSent = false;

    @Column(name = "reminder_sent", nullable = false)
    private Boolean reminderSent = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public ValidatorAssignment() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.assignedAt = LocalDateTime.now();
    }

    public ValidatorAssignment(Validator validator, Evidence evidence, User assignedBy) {
        this();
        this.validator = validator;
        this.evidence = evidence;
        this.assignedBy = assignedBy;
    }

    // Business methods
    public boolean isActive() {
        return status == AssignmentStatus.ASSIGNED || status == AssignmentStatus.ACCEPTED || status == AssignmentStatus.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return status == AssignmentStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return status == AssignmentStatus.CANCELLED || status == AssignmentStatus.REJECTED;
    }

    public boolean isOverdue() {
        return dueDate != null && LocalDateTime.now().isAfter(dueDate) && isActive();
    }

    public boolean isPendingAcceptance() {
        return status == AssignmentStatus.ASSIGNED;
    }

    public void accept() {
        if (status == AssignmentStatus.ASSIGNED) {
            status = AssignmentStatus.ACCEPTED;
            acceptedAt = LocalDateTime.now();
            updateTimestamp();
        }
    }

    public void start() {
        if (status == AssignmentStatus.ACCEPTED) {
            status = AssignmentStatus.IN_PROGRESS;
            startedAt = LocalDateTime.now();
            updateTimestamp();
        }
    }

    public void complete(String notes) {
        if (isActive()) {
            status = AssignmentStatus.COMPLETED;
            completedAt = LocalDateTime.now();
            completionNotes = notes;
            updateTimestamp();
        }
    }

    public void cancel(String reason) {
        if (isActive()) {
            status = AssignmentStatus.CANCELLED;
            completionNotes = reason;
            updateTimestamp();
        }
    }

    public void reject(String reason) {
        if (status == AssignmentStatus.ASSIGNED) {
            status = AssignmentStatus.REJECTED;
            completionNotes = reason;
            updateTimestamp();
        }
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
        updateTimestamp();
    }

    public void setPriority(ValidationPriority priority) {
        this.priority = priority;
        updateTimestamp();
    }

    public void markNotificationSent() {
        this.notificationSent = true;
        updateTimestamp();
    }

    public void markReminderSent() {
        this.reminderSent = true;
        updateTimestamp();
    }

    public long getDurationInHours() {
        if (startedAt != null && completedAt != null) {
            return java.time.Duration.between(startedAt, completedAt).toHours();
        }
        return 0;
    }

    public long getTimeToAcceptanceInHours() {
        if (assignedAt != null && acceptedAt != null) {
            return java.time.Duration.between(assignedAt, acceptedAt).toHours();
        }
        return 0;
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTimestamp();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public Validator getValidator() { return validator; }
    public void setValidator(Validator validator) { this.validator = validator; }

    public Evidence getEvidence() { return evidence; }
    public void setEvidence(Evidence evidence) { this.evidence = evidence; }

    public User getAssignedBy() { return assignedBy; }
    public void setAssignedBy(User assignedBy) { this.assignedBy = assignedBy; }

    public AssignmentStatus getStatus() { return status; }
    public void setStatus(AssignmentStatus status) { this.status = status; updateTimestamp(); }

    public ValidationPriority getPriority() { return priority; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public LocalDateTime getDueDate() { return dueDate; }

    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; updateTimestamp(); }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValidatorAssignment)) return false;
        ValidatorAssignment that = (ValidatorAssignment) o;
        return uuid != null && uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ValidatorAssignment{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", assignedAt=" + assignedAt +
                ", dueDate=" + dueDate +
                '}';
    }
}
