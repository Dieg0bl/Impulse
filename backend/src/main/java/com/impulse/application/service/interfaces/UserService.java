package com.impulse.application.service.interfaces;

import com.impulse.application.dto.common.PaginationRequest;
import com.impulse.application.dto.common.PaginationResponse;
import com.impulse.application.dto.user.UserResponseDto;
import com.impulse.application.dto.user.UserCreateRequestDto;
import com.impulse.application.dto.user.UserUpdateRequestDto;

import java.util.List;

/**
 * Interface for User Service operations
 */
public interface UserService {

    /**
     * Create a new user
     */
    UserResponseDto createUser(UserCreateRequestDto request);

    /**
     * Update existing user
     */
    UserResponseDto updateUser(Long userId, UserUpdateRequestDto request);

    /**
     * Get user by ID
     */
    UserResponseDto getUserById(Long id);

    /**
     * Get user by UUID
     */
    UserResponseDto getUserByUuid(String uuid);

    /**
     * Get user by username
     */
    UserResponseDto getUserByUsername(String username);

    /**
     * Get user by email
     */
    UserResponseDto getUserByEmail(String email);

    /**
     * Get all users with pagination
     */
    PaginationResponse<UserResponseDto> getAllUsers(PaginationRequest request);

    /**
     * Delete user
     */
    void deleteUser(Long userId);

    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Check if user exists by username
     */
    boolean existsByUsername(String username);

    /**
     * Activate user account
     */
    void activateUser(Long userId);

    /**
     * Deactivate user account
     */
    void deactivateUser(Long userId);

    /**
     * Check if user is active
     */
    boolean isUserActive(Long userId);

    /**
     * Update user last login
     */
    void updateLastLogin(Long userId);

    /**
     * Search users by name or username
     */
    PaginationResponse<UserResponseDto> searchUsers(String query, PaginationRequest request);

    /**
     * Get user statistics
     */
    UserStatsDto getUserStatistics(Long userId);

    /**
     * Get top users by activity
     */
    List<UserResponseDto> getTopActiveUsers(int limit);

    // Inner class for user statistics
    class UserStatsDto {
        private Long totalChallenges;
        private Long activeChallenges;
        private Long completedChallenges;
        private Long totalEvidence;
        private Long approvedEvidence;
        private Double successRate;

        // Constructors
        public UserStatsDto() {}

        public UserStatsDto(Long totalChallenges, Long activeChallenges, Long completedChallenges,
                           Long totalEvidence, Long approvedEvidence) {
            this.totalChallenges = totalChallenges;
            this.activeChallenges = activeChallenges;
            this.completedChallenges = completedChallenges;
            this.totalEvidence = totalEvidence;
            this.approvedEvidence = approvedEvidence;
            this.successRate = totalEvidence > 0 ?
                (approvedEvidence.doubleValue() / totalEvidence.doubleValue()) * 100 : 0.0;
        }

        // Getters and setters
        public Long getTotalChallenges() { return totalChallenges; }
        public void setTotalChallenges(Long totalChallenges) { this.totalChallenges = totalChallenges; }

        public Long getActiveChallenges() { return activeChallenges; }
        public void setActiveChallenges(Long activeChallenges) { this.activeChallenges = activeChallenges; }

        public Long getCompletedChallenges() { return completedChallenges; }
        public void setCompletedChallenges(Long completedChallenges) { this.completedChallenges = completedChallenges; }

        public Long getTotalEvidence() { return totalEvidence; }
        public void setTotalEvidence(Long totalEvidence) { this.totalEvidence = totalEvidence; }

        public Long getApprovedEvidence() { return approvedEvidence; }
        public void setApprovedEvidence(Long approvedEvidence) { this.approvedEvidence = approvedEvidence; }

        public Double getSuccessRate() { return successRate; }
        public void setSuccessRate(Double successRate) { this.successRate = successRate; }
    }
}
