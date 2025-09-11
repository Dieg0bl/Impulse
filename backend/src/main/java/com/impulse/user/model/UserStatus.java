package com.impulse.user.model;

/**
 * IMPULSE LEAN v1 - User Status Enumeration
 * 
 * Defines user account states for lifecycle management
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum UserStatus {
    
    /**
     * Account created but email not verified yet
     */
    PENDING("Pending Email Verification"),
    
    /**
     * Active account - can fully use the platform
     */
    ACTIVE("Active"),
    
    /**
     * Temporarily suspended account
     */
    SUSPENDED("Suspended"),
    
    /**
     * Permanently banned account
     */
    BANNED("Banned"),
    
    /**
     * Soft deleted account (GDPR compliance)
     */
    DELETED("Deleted");

    private final String displayName;

    UserStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if user can login with this status
     */
    public boolean canLogin() {
        return this == ACTIVE;
    }

    /**
     * Check if user can perform any action
     */
    public boolean isUsable() {
        return this == ACTIVE || this == PENDING;
    }

    /**
     * Check if account is in a blocked state
     */
    public boolean isBlocked() {
        return this == SUSPENDED || this == BANNED || this == DELETED;
    }
}
