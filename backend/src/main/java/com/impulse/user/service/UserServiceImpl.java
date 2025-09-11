package com.impulse.user.service;

import com.impulse.user.service.UserService;
import com.impulse.user.model.User;
import com.impulse.user.model.UserRole;
import com.impulse.user.model.UserStatus;
import com.impulse.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * IMPULSE LEAN v1 - User Service Implementation
 * 
 * Business logic implementation for user operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Basic CRUD operations
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUuid(String uuid) {
        return userRepository.findByUuid(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    // Business operations
    @Override
    public User createUser(String username, String email, String password, 
                          String firstName, String lastName) {
        logger.info("Creating new user with username: {}", username);

        // Validate uniqueness
        if (!isUsernameAvailable(username)) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (!isEmailAvailable(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // Create user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.PENDING);
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);
        logger.info("User created successfully with UUID: {}", savedUser.getUuid());

        return savedUser;
    }

    @Override
    public User updateUser(User user) {
        logger.info("Updating user: {}", user.getUuid());
        return userRepository.save(user);
    }

    @Override
    public User updateUserProfile(String uuid, String firstName, String lastName, String bio) {
        logger.info("Updating profile for user: {}", uuid);

        User user = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBio(bio);

        return userRepository.save(user);
    }

    @Override
    public User updateUserStatus(String uuid, UserStatus status) {
        logger.info("Updating status for user {} to {}", uuid, status);

        User user = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setStatus(status);
        
        // If activating user, unlock and reset failed attempts
        if (status == UserStatus.ACTIVE) {
            user.unlockAccount();
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUserRole(String uuid, UserRole role) {
        logger.info("Updating role for user {} to {}", uuid, role);

        User user = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setRole(role);
        return userRepository.save(user);
    }

    // Authentication and security
    @Override
    @Transactional(readOnly = true)
    public boolean validatePassword(String username, String rawPassword) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }

    @Override
    public User authenticateUser(String username, String password) {
        logger.info("Authenticating user: {}", username);

        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        User user = userOpt.get();

        // Check if user can login
        if (!user.canLogin()) {
            throw new IllegalStateException("User cannot login - account locked or not verified");
        }

        // Validate password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            // Increment failed attempts
            user.incrementFailedLoginAttempts();
            
            // Lock account if too many failed attempts
            if (user.getFailedLoginAttempts() >= 5) {
                user.lockAccount(LocalDateTime.now().plusHours(1));
                logger.warn("User account locked due to failed login attempts: {}", username);
            }
            
            userRepository.save(user);
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Reset failed attempts on successful login
        user.resetFailedLoginAttempts();
        user.setLastLoginAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    @Override
    public void lockUser(String uuid, String reason) {
        logger.info("Locking user {} with reason: {}", uuid, reason);

        User user = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.lockAccount(LocalDateTime.now().plusDays(30)); // Lock for 30 days
        userRepository.save(user);
    }

    @Override
    public void unlockUser(String uuid) {
        logger.info("Unlocking user: {}", uuid);

        User user = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.unlockAccount();
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserLocked(String uuid) {
        User user = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return user.isLocked();
    }

    // Email verification
    @Override
    public void sendEmailVerification(String uuid) {
        logger.info("Sending email verification for user: {}", uuid);
        // TODO: Implement email sending logic
        // This would typically generate a verification token and send email
    }

    @Override
    public boolean verifyEmail(String uuid, String token) {
        logger.info("Verifying email for user: {}", uuid);

        User user = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // TODO: Implement token validation logic
        // For now, just set as verified
        user.setEmailVerified(true);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailVerified(String uuid) {
        User user = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return Boolean.TRUE.equals(user.getEmailVerified());
    }

    // Query operations
    @Override
    @Transactional(readOnly = true)
    public List<User> findActiveUsers() {
        return userRepository.findActiveUsers();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersByStatus(UserStatus status) {
        return userRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String searchTerm, Pageable pageable) {
        return userRepository.searchUsers(searchTerm, pageable);
    }

    // Statistics and analytics
    @Override
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepository.countByStatus(UserStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUserCountByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUserCountByStatus(UserStatus status) {
        return userRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getTopChallengeParticipants(int limit) {
        return userRepository.findMostActiveChallengeParticipants(PageRequest.of(0, limit));
    }

    // Business validation
    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserParticipateInChallenge(String userUuid, String challengeUuid) {
        // TODO: Implement challenge participation logic
        // This would check if user is active, not banned from challenge, etc.
        User user = findByUuid(userUuid)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return user.isActive();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserCompletedChallenge(String userUuid, String challengeUuid) {
        // TODO: Implement completion check logic
        return false;
    }

    // Profile management
    @Override
    public User updateProfilePicture(String uuid, String profilePictureUrl) {
        logger.info("Updating profile picture for user: {}", uuid);

        User user = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setAvatarUrl(profilePictureUrl);
        return userRepository.save(user);
    }

    @Override
    public User updateUserPreferences(String uuid, boolean marketingConsent, boolean privacyConsent) {
        logger.info("Updating preferences for user: {}", uuid);

        User user = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setMarketingConsent(marketingConsent);
        user.setPrivacyConsent(privacyConsent);
        
        if (privacyConsent) {
            user.setGdprConsentDate(LocalDateTime.now());
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUserAccount(String uuid) {
        logger.info("Deleting user account: {}", uuid);

        User user = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Soft delete by setting status
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    @Override
    public void anonymizeUserData(String uuid) {
        logger.info("Anonymizing user data: {}", uuid);

        User user = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Anonymize personal data
        user.setFirstName("Anonymous");
        user.setLastName("User");
        user.setEmail("anonymized@example.com");
        user.setBio(null);
        user.setPhone(null);
        user.setAvatarUrl(null);
        user.setStatus(UserStatus.DELETED);

        userRepository.save(user);
    }

    // Admin operations
    @Override
    @Transactional(readOnly = true)
    public List<User> findModerators() {
        return userRepository.findActiveModerators();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAdmins() {
        return userRepository.findActiveAdmins();
    }

    @Override
    public User promoteToModerator(String uuid) {
        return updateUserRole(uuid, UserRole.MODERATOR);
    }

    @Override
    public User demoteFromModerator(String uuid) {
        return updateUserRole(uuid, UserRole.USER);
    }

    @Override
    public void bulkUpdateUserStatus(List<String> userUuids, UserStatus status) {
        logger.info("Bulk updating status for {} users to {}", userUuids.size(), status);

        for (String uuid : userUuids) {
            try {
                updateUserStatus(uuid, status);
            } catch (Exception e) {
                logger.error("Failed to update status for user {}: {}", uuid, e.getMessage());
            }
        }
    }

    // Password management
    @Override
    public void requestPasswordReset(String email) {
        logger.info("Password reset requested for email: {}", email);

        Optional<User> userOpt = findByEmail(email);
        if (userOpt.isEmpty()) {
            // Don't reveal if email exists for security
            return;
        }

        // TODO: Generate reset token and send email
        logger.info("Password reset token generated for user: {}", userOpt.get().getUuid());
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        logger.info("Resetting password with token");

        // TODO: Validate token and find user
        // For now, return false
        return false;
    }

    @Override
    public boolean changePassword(String uuid, String oldPassword, String newPassword) {
        logger.info("Changing password for user: {}", uuid);

        User user = findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate old password
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid current password");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return true;
    }
}
