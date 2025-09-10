package com.impulse.lean.infrastructure.security;

import com.impulse.lean.domain.model.Challenge;
import com.impulse.lean.domain.model.ChallengeParticipation;
import com.impulse.lean.domain.model.Evidence;
import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.model.UserRole;
import com.impulse.lean.infrastructure.security.CustomUserDetailsService.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * IMPULSE LEAN v1 - Authorization Service
 * 
 * Handles complex authorization logic for domain entities
 * Implements RBAC with resource-based permissions
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Component
public class AuthorizationService {

    /**
     * Check if current user can access user profile
     */
    public boolean canAccessUserProfile(Long targetUserId) {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        if (principal == null) return false;

        // Admin can access any profile
        if (isAdmin(principal)) return true;
        
        // Users can access their own profile
        return principal.getUserId().equals(targetUserId);
    }

    /**
     * Check if current user can modify user profile
     */
    public boolean canModifyUserProfile(Long targetUserId) {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        if (principal == null) return false;

        // Admin can modify any profile
        if (isAdmin(principal)) return true;
        
        // Users can modify their own profile
        return principal.getUserId().equals(targetUserId);
    }

    /**
     * Check if current user can manage challenges
     */
    public boolean canManageChallenges() {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        if (principal == null) return false;

        // Admin, Super Admin and Moderator can manage challenges
        UserRole role = principal.getUserRole();
        return role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN || role == UserRole.MODERATOR;
    }

    /**
     * Check if current user can modify specific challenge
     */
    public boolean canModifyChallenge(Challenge challenge) {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        if (principal == null) return false;

        // Admin can modify any challenge
        if (isAdmin(principal)) return true;
        
        // Creator can modify their own challenge
        return challenge.getCreator().getId().equals(principal.getUserId());
    }

    /**
     * Check if current user can participate in challenge
     */
    public boolean canParticipateInChallenge(Challenge challenge) {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        if (principal == null) return false;

        // Admin cannot participate (conflict of interest)
        if (isAdmin(principal)) return false;
        
        // Creator cannot participate in their own challenge
        if (challenge.getCreator().getId().equals(principal.getUserId())) return false;
        
        // Regular users and validators can participate
        UserRole role = principal.getUserRole();
        return role == UserRole.USER || role == UserRole.VALIDATOR;
    }

    /**
     * Check if current user can submit evidence
     */
    public boolean canSubmitEvidence(ChallengeParticipation participation) {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        if (principal == null) return false;

        // Only the participant can submit evidence
        return participation.getUser().getId().equals(principal.getUserId());
    }

    /**
     * Check if current user can validate evidence
     */
    public boolean canValidateEvidence(Evidence evidence) {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        if (principal == null) return false;

        // Admin can validate any evidence
        if (isAdmin(principal)) return true;
        
        // Validators can validate evidence (but not their own)
        UserRole role = principal.getUserRole();
        if (role == UserRole.VALIDATOR) {
            return !evidence.getParticipation().getUser().getId().equals(principal.getUserId());
        }
        
        return false;
    }

    /**
     * Check if current user can access admin panel
     */
    public boolean canAccessAdminPanel() {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        return principal != null && isAdmin(principal);
    }

    /**
     * Check if current user can manage users
     */
    public boolean canManageUsers() {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        return principal != null && isAdmin(principal);
    }

    /**
     * Check if current user can view analytics
     */
    public boolean canViewAnalytics() {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        if (principal == null) return false;

        // Admin and Super Admin can view analytics
        UserRole role = principal.getUserRole();
        return role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN;
    }

    /**
     * Check if current user can moderate content
     */
    public boolean canModerateContent() {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        if (principal == null) return false;

        // Admin, Super Admin and Moderator can moderate
        UserRole role = principal.getUserRole();
        return role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN || role == UserRole.MODERATOR;
    }

    /**
     * Check if current user can view user list
     */
    public boolean canViewUserList() {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        if (principal == null) return false;

        // Admin, Super Admin, Moderator and Validators can view user list
        UserRole role = principal.getUserRole();
        return role == UserRole.ADMIN || 
               role == UserRole.SUPER_ADMIN || 
               role == UserRole.MODERATOR ||
               role == UserRole.VALIDATOR;
    }

    /**
     * Get current authenticated user
     */
    public User getCurrentUser() {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        return principal != null ? principal.getUser() : null;
    }

    /**
     * Get current user ID
     */
    public Long getCurrentUserId() {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        return principal != null ? principal.getUserId() : null;
    }

    /**
     * Check if user is admin
     */
    public boolean isCurrentUserAdmin() {
        CustomUserPrincipal principal = getCurrentUserPrincipal();
        return principal != null && isAdmin(principal);
    }

    // Private helper methods
    private CustomUserPrincipal getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserPrincipal) {
            return (CustomUserPrincipal) principal;
        }
        
        return null;
    }

    private boolean isAdmin(CustomUserPrincipal principal) {
        return principal.getUserRole() == UserRole.ADMIN;
    }
}
