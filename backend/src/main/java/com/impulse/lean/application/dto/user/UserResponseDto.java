package com.impulse.lean.application.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.model.UserRole;
import com.impulse.lean.domain.model.UserStatus;

import java.time.LocalDateTime;

/**
 * IMPULSE LEAN v1 - User Response DTO
 * 
 * User data transfer object for API responses
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {

    private Long id;
    private String uuid;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private UserRole role;
    private UserStatus status;
    private String profilePictureUrl;
    private String bio;
    private boolean emailVerified;
    private long challengeCount;
    private long completedChallengeCount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastLoginAt;

    // Constructors
    public UserResponseDto() {}

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.uuid = user.getUuid();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.fullName = user.getFullName();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.profilePictureUrl = user.getAvatarUrl();
        this.bio = user.getBio();
        this.emailVerified = Boolean.TRUE.equals(user.getEmailVerified());
        this.createdAt = user.getCreatedAt();
        this.lastLoginAt = user.getLastLoginAt();
        
        // Calculate challenge counts if available
        if (user.getChallengeParticipations() != null) {
            this.challengeCount = user.getChallengeParticipations().size();
            this.completedChallengeCount = user.getChallengeParticipations().stream()
                .mapToLong(p -> p.getStatus() == com.impulse.lean.domain.model.ParticipationStatus.COMPLETED ? 1 : 0)
                .sum();
        }
    }

    // Static factory methods
    public static UserResponseDto from(User user) {
        return new UserResponseDto(user);
    }

    public static UserResponseDto fromWithoutSensitiveData(User user) {
        UserResponseDto dto = new UserResponseDto(user);
        dto.email = null; // Hide email for public endpoints
        return dto;
    }

    // Business methods
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isModerator() {
        return role == UserRole.MODERATOR || role == UserRole.ADMIN;
    }

    public String getDisplayName() {
        if (fullName != null && !fullName.trim().isEmpty()) {
            return fullName;
        }
        return username;
    }

    public String getRoleDisplayName() {
        return role != null ? role.getDisplayName() : "Unknown";
    }

    public String getStatusDisplayName() {
        return status != null ? status.getDisplayName() : "Unknown";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public long getChallengeCount() { return challengeCount; }
    public void setChallengeCount(long challengeCount) { this.challengeCount = challengeCount; }

    public long getCompletedChallengeCount() { return completedChallengeCount; }
    public void setCompletedChallengeCount(long completedChallengeCount) { this.completedChallengeCount = completedChallengeCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}
