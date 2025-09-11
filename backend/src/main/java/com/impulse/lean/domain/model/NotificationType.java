package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Notification Type Enum
 * 
 * Represents different types of notifications in the system
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum NotificationType {
    
    // Evidence-related notifications
    EVIDENCE_SUBMITTED("Evidence Submitted", "New evidence has been submitted for review"),
    EVIDENCE_APPROVED("Evidence Approved", "Your evidence has been approved"),
    EVIDENCE_REJECTED("Evidence Rejected", "Your evidence has been rejected"),
    EVIDENCE_NEEDS_REVISION("Evidence Needs Revision", "Your evidence requires revision"),
    EVIDENCE_VALIDATION_ASSIGNED("Validation Assigned", "Evidence has been assigned for validation"),
    
    // Validation-related notifications
    VALIDATION_REQUEST("Validation Request", "You have been assigned to validate evidence"),
    VALIDATION_REMINDER("Validation Reminder", "Reminder: Evidence validation is pending"),
    VALIDATION_OVERDUE("Validation Overdue", "Evidence validation is overdue"),
    VALIDATION_COMPLETED("Validation Completed", "Evidence validation has been completed"),
    VALIDATION_ESCALATED("Validation Escalated", "Evidence validation has been escalated"),
    
    // Challenge-related notifications
    CHALLENGE_CREATED("Challenge Created", "A new challenge has been created"),
    CHALLENGE_UPDATED("Challenge Updated", "Challenge has been updated"),
    CHALLENGE_DEADLINE_APPROACHING("Challenge Deadline", "Challenge deadline is approaching"),
    CHALLENGE_COMPLETED("Challenge Completed", "Challenge has been completed"),
    CHALLENGE_CANCELLED("Challenge Cancelled", "Challenge has been cancelled"),
    
    // User-related notifications
    USER_REGISTRATION("User Registration", "Welcome to IMPULSE LEAN"),
    USER_PROFILE_UPDATED("Profile Updated", "Your profile has been updated"),
    USER_ROLE_CHANGED("Role Changed", "Your role has been changed"),
    USER_ACCOUNT_SUSPENDED("Account Suspended", "Your account has been suspended"),
    USER_ACCOUNT_ACTIVATED("Account Activated", "Your account has been activated"),
    
    // Validator-related notifications
    VALIDATOR_APPLICATION_SUBMITTED("Validator Application", "Validator application submitted"),
    VALIDATOR_APPLICATION_APPROVED("Validator Approved", "Your validator application has been approved"),
    VALIDATOR_APPLICATION_REJECTED("Validator Rejected", "Your validator application has been rejected"),
    VALIDATOR_ASSIGNMENT_ACCEPTED("Assignment Accepted", "Validation assignment has been accepted"),
    VALIDATOR_ASSIGNMENT_DECLINED("Assignment Declined", "Validation assignment has been declined"),
    
    // System notifications
    SYSTEM_MAINTENANCE("System Maintenance", "System maintenance notification"),
    SYSTEM_UPDATE("System Update", "System has been updated"),
    SYSTEM_ALERT("System Alert", "Important system alert"),
    SYSTEM_ANNOUNCEMENT("System Announcement", "General system announcement"),
    
    // Performance notifications
    PERFORMANCE_REPORT("Performance Report", "Your performance report is ready"),
    PERFORMANCE_MILESTONE("Performance Milestone", "You have reached a performance milestone"),
    PERFORMANCE_WARNING("Performance Warning", "Performance warning notification"),
    
    // Quality control notifications
    QUALITY_REVIEW_REQUIRED("Quality Review", "Quality review is required"),
    QUALITY_ISSUE_DETECTED("Quality Issue", "Quality issue has been detected"),
    QUALITY_IMPROVEMENT("Quality Improvement", "Quality improvement suggestion"),
    
    // Administrative notifications
    ADMIN_ACTION_REQUIRED("Admin Action Required", "Administrative action is required"),
    ADMIN_REPORT_READY("Admin Report Ready", "Administrative report is ready"),
    ADMIN_ALERT("Admin Alert", "Administrative alert"),
    
    // General notifications
    GENERAL_INFO("General Information", "General information notification"),
    GENERAL_WARNING("General Warning", "General warning notification"),
    GENERAL_SUCCESS("General Success", "General success notification"),
    GENERAL_ERROR("General Error", "General error notification");

    private final String displayName;
    private final String description;

    NotificationType(String displayName, String description) {
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
     * Get notification category
     */
    public NotificationCategory getCategory() {
        switch (this) {
            case EVIDENCE_SUBMITTED:
            case EVIDENCE_APPROVED:
            case EVIDENCE_REJECTED:
            case EVIDENCE_NEEDS_REVISION:
            case EVIDENCE_VALIDATION_ASSIGNED:
                return NotificationCategory.EVIDENCE;
                
            case VALIDATION_REQUEST:
            case VALIDATION_REMINDER:
            case VALIDATION_OVERDUE:
            case VALIDATION_COMPLETED:
            case VALIDATION_ESCALATED:
                return NotificationCategory.VALIDATION;
                
            case CHALLENGE_CREATED:
            case CHALLENGE_UPDATED:
            case CHALLENGE_DEADLINE_APPROACHING:
            case CHALLENGE_COMPLETED:
            case CHALLENGE_CANCELLED:
                return NotificationCategory.CHALLENGE;
                
            case USER_REGISTRATION:
            case USER_PROFILE_UPDATED:
            case USER_ROLE_CHANGED:
            case USER_ACCOUNT_SUSPENDED:
            case USER_ACCOUNT_ACTIVATED:
                return NotificationCategory.USER;
                
            case VALIDATOR_APPLICATION_SUBMITTED:
            case VALIDATOR_APPLICATION_APPROVED:
            case VALIDATOR_APPLICATION_REJECTED:
            case VALIDATOR_ASSIGNMENT_ACCEPTED:
            case VALIDATOR_ASSIGNMENT_DECLINED:
                return NotificationCategory.VALIDATOR;
                
            case SYSTEM_MAINTENANCE:
            case SYSTEM_UPDATE:
            case SYSTEM_ALERT:
            case SYSTEM_ANNOUNCEMENT:
                return NotificationCategory.SYSTEM;
                
            case PERFORMANCE_REPORT:
            case PERFORMANCE_MILESTONE:
            case PERFORMANCE_WARNING:
                return NotificationCategory.PERFORMANCE;
                
            case QUALITY_REVIEW_REQUIRED:
            case QUALITY_ISSUE_DETECTED:
            case QUALITY_IMPROVEMENT:
                return NotificationCategory.QUALITY;
                
            case ADMIN_ACTION_REQUIRED:
            case ADMIN_REPORT_READY:
            case ADMIN_ALERT:
                return NotificationCategory.ADMIN;
                
            default:
                return NotificationCategory.GENERAL;
        }
    }

    /**
     * Check if this notification type requires immediate attention
     */
    public boolean isUrgent() {
        switch (this) {
            case VALIDATION_OVERDUE:
            case SYSTEM_ALERT:
            case USER_ACCOUNT_SUSPENDED:
            case QUALITY_ISSUE_DETECTED:
            case ADMIN_ACTION_REQUIRED:
            case GENERAL_ERROR:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if this notification type should be sent via email
     */
    public boolean shouldSendEmail() {
        switch (this) {
            case EVIDENCE_APPROVED:
            case EVIDENCE_REJECTED:
            case VALIDATION_REQUEST:
            case VALIDATION_OVERDUE:
            case CHALLENGE_DEADLINE_APPROACHING:
            case USER_REGISTRATION:
            case VALIDATOR_APPLICATION_APPROVED:
            case VALIDATOR_APPLICATION_REJECTED:
            case SYSTEM_MAINTENANCE:
            case ADMIN_ACTION_REQUIRED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Notification categories for grouping
     */
    public enum NotificationCategory {
        EVIDENCE, VALIDATION, CHALLENGE, USER, VALIDATOR, 
        SYSTEM, PERFORMANCE, QUALITY, ADMIN, GENERAL
    }
}
