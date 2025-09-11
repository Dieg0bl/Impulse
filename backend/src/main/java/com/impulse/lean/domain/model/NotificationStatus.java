package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Notification Status Enum
 * 
 * Represents the status of notifications in the system
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum NotificationStatus {
    
    PENDING("Pending", "Notification is pending to be sent"),
    SCHEDULED("Scheduled", "Notification is scheduled for delivery"),
    SENDING("Sending", "Notification is currently being sent"),
    SENT("Sent", "Notification has been sent successfully"),
    DELIVERED("Delivered", "Notification has been delivered"),
    READ("Read", "Notification has been read by recipient"),
    FAILED("Failed", "Notification delivery failed"),
    CANCELLED("Cancelled", "Notification has been cancelled"),
    EXPIRED("Expired", "Notification has expired");

    private final String displayName;
    private final String description;

    NotificationStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if notification is in a final state
     */
    public boolean isFinal() {
        switch (this) {
            case DELIVERED:
            case READ:
            case FAILED:
            case CANCELLED:
            case EXPIRED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if notification is active (can be processed)
     */
    public boolean isActive() {
        switch (this) {
            case PENDING:
            case SCHEDULED:
            case SENDING:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if notification was successfully processed
     */
    public boolean isSuccessful() {
        switch (this) {
            case SENT:
            case DELIVERED:
            case READ:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if notification can be retried
     */
    public boolean canRetry() {
        switch (this) {
            case FAILED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if notification can be cancelled
     */
    public boolean canCancel() {
        switch (this) {
            case PENDING:
            case SCHEDULED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Get the next possible statuses from current status
     */
    public NotificationStatus[] getNextPossibleStatuses() {
        switch (this) {
            case PENDING:
                return new NotificationStatus[]{SCHEDULED, SENDING, CANCELLED};
            case SCHEDULED:
                return new NotificationStatus[]{SENDING, CANCELLED, EXPIRED};
            case SENDING:
                return new NotificationStatus[]{SENT, FAILED};
            case SENT:
                return new NotificationStatus[]{DELIVERED, FAILED};
            case DELIVERED:
                return new NotificationStatus[]{READ};
            case READ:
            case FAILED:
            case CANCELLED:
            case EXPIRED:
                return new NotificationStatus[]{};
            default:
                return new NotificationStatus[]{};
        }
    }

    /**
     * Check if transition to another status is valid
     */
    public boolean canTransitionTo(NotificationStatus newStatus) {
        NotificationStatus[] possibleStatuses = getNextPossibleStatuses();
        for (NotificationStatus status : possibleStatuses) {
            if (status == newStatus) {
                return true;
            }
        }
        return false;
    }
}
