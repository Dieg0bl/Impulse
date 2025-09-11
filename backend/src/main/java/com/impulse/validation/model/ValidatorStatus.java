package com.impulse.validation.model;

/**
 * IMPULSE LEAN v1 - Validator Status Enum
 * 
 * Possible statuses for validators
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum ValidatorStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    SUSPENDED("Suspended"),
    PENDING_APPROVAL("Pending Approval"),
    REJECTED("Rejected");

    private final String displayName;

    ValidatorStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canValidate() {
        return this == ACTIVE;
    }

    public boolean isPending() {
        return this == PENDING_APPROVAL;
    }

    public boolean isBlocked() {
        return this == SUSPENDED || this == REJECTED || this == INACTIVE;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
