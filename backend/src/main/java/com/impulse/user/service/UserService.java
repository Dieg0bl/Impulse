package com.impulse.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.impulse.user.model.User;
import com.impulse.user.model.UserRole;
import com.impulse.user.model.UserStatus;

/**
 * IMPULSE LEAN v1 - User Service Interface
 * 
 * Business logic interface for user operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public interface UserService {

    // Basic CRUD operations
    Optional<User> findById(Long id);
    Optional<User> findByUuid(String uuid);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User save(User user);
    void deleteById(Long id);

    // Business operations
    User createUser(String username, String email, String password, String firstName, String lastName);
    User updateUser(User user);
    User updateUserProfile(String uuid, String firstName, String lastName, String bio);
    User updateUserStatus(String uuid, UserStatus status);
    User updateUserRole(String uuid, UserRole role);

    // Authentication and security
    boolean validatePassword(String username, String rawPassword);
    User authenticateUser(String username, String password);
    void lockUser(String uuid, String reason);
    void unlockUser(String uuid);
    boolean isUserLocked(String uuid);

    // Email verification
    void sendEmailVerification(String uuid);
    boolean verifyEmail(String uuid, String token);
    boolean isEmailVerified(String uuid);

    // Query operations
    List<User> findActiveUsers();
    List<User> findUsersByRole(UserRole role);
    List<User> findUsersByStatus(UserStatus status);
    Page<User> findUsers(Pageable pageable);
    Page<User> searchUsers(String searchTerm, Pageable pageable);

    // Statistics and analytics
    long getTotalUserCount();
    long getActiveUserCount();
    long getUserCountByRole(UserRole role);
    long getUserCountByStatus(UserStatus status);
    List<User> getTopChallengeParticipants(int limit);

    // Business validation
    boolean isUsernameAvailable(String username);
    boolean isEmailAvailable(String email);
    boolean canUserParticipateInChallenge(String userUuid, String challengeUuid);
    boolean hasUserCompletedChallenge(String userUuid, String challengeUuid);

    // Profile management
    User updateProfilePicture(String uuid, String profilePictureUrl);
    User updateUserPreferences(String uuid, boolean marketingConsent, boolean privacyConsent);
    void deleteUserAccount(String uuid);
    void anonymizeUserData(String uuid);

    // Admin operations
    List<User> findModerators();
    List<User> findAdmins();
    User promoteToModerator(String uuid);
    User demoteFromModerator(String uuid);
    void bulkUpdateUserStatus(List<String> userUuids, UserStatus status);

    // Password management
    void requestPasswordReset(String email);
    boolean resetPassword(String token, String newPassword);
    boolean changePassword(String uuid, String oldPassword, String newPassword);
}
