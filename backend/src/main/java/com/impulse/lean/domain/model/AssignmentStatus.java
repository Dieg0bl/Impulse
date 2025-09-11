package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Assignment Status Enum
 * 
 * Status of validator assignments
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum AssignmentStatus {
    ASSIGNED("Assigned", "Assignment has been created and assigned to validator"),
    ACCEPTED("Accepted", "Validator has accepted the assignment"),
    IN_PROGRESS("In Progress", "Validator is working on the validation"),
    COMPLETED("Completed", "Validation has been completed"),
    REJECTED("Rejected", "Validator has rejected the assignment"),
    CANCELLED("Cancelled", "Assignment has been cancelled"),
    EXPIRED("Expired", "Assignment has expired due to timeout");

    private final String displayName;
    private final String description;

    AssignmentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ASSIGNED || this == ACCEPTED || this == IN_PROGRESS;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isClosed() {
        return this == COMPLETED || this == REJECTED || this == CANCELLED || this == EXPIRED;
    }

    public boolean canBeUpdated() {
        return this == ASSIGNED || this == ACCEPTED || this == IN_PROGRESS;
    }

    public boolean canBeAccepted() {
        return this == ASSIGNED;
    }

    public boolean canBeStarted() {
        return this == ACCEPTED;
    }

    public boolean canBeCompleted() {
        return isActive();
    }

    public boolean canBeCancelled() {
        return isActive();
    }

    @Override
    public String toString() {
        return displayName;
    }
}
