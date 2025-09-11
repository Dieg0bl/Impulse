package com.impulse.user.model;

/**
 * IMPULSE LEAN v1 - User Role Enumeration
 * 
 * Defines the role-based access control (RBAC) system
 * Maximum 7 roles as per requirements
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum UserRole {
    
    /**
     * Guest user - Limited read access to public content
     */
    GUEST("Guest", 0),
    
    /**
     * Regular user - Full participation in challenges
     */
    USER("User", 1),
    
    /**
     * Validator - Can validate evidence + USER permissions
     */
    VALIDATOR("Validator", 2),
    
    /**
     * Moderator - Content moderation + VALIDATOR permissions
     */
    MODERATOR("Moderator", 3),
    
    /**
     * Administrator - System administration + MODERATOR permissions
     */
    ADMIN("Administrator", 4),
    
    /**
     * Super Administrator - Full system access
     */
    SUPER_ADMIN("Super Administrator", 5);

    private final String displayName;
    private final int level;

    UserRole(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Check if this role has at least the specified role level
     */
    public boolean hasLevel(UserRole requiredRole) {
        return this.level >= requiredRole.level;
    }

    /**
     * Check if this role can perform admin operations
     */
    public boolean isAdmin() {
        return this == ADMIN || this == SUPER_ADMIN;
    }

    /**
     * Check if this role can moderate content
     */
    public boolean canModerate() {
        return hasLevel(MODERATOR);
    }

    /**
     * Check if this role can validate evidence
     */
    public boolean canValidate() {
        return hasLevel(VALIDATOR);
    }

    /**
     * Check if this role can participate in challenges
     */
    public boolean canParticipate() {
        return hasLevel(USER);
    }
}
