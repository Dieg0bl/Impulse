package com.impulse.application.dto.user;

import java.time.LocalDateTime;

/**
 * DTO for User Response
 */
public class UserResponseDto {
    private Long id;
    private String uuid;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private String bio;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    private Boolean isActive;
    private Boolean isVerified;
    private Integer challengesCreated;
    private Integer challengesCompleted;
    private Integer totalPoints;

    // Constructors
    public UserResponseDto() {}

    public UserResponseDto(Long id, String uuid, String username, String email,
                          String firstName, String lastName, String profileImageUrl,
                          String bio, String status, LocalDateTime createdAt,
                          LocalDateTime updatedAt, LocalDateTime lastLoginAt,
                          Boolean isActive, Boolean isVerified,
                          Integer challengesCreated, Integer challengesCompleted,
                          Integer totalPoints) {
        this.id = id;
        this.uuid = uuid;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLoginAt = lastLoginAt;
        this.isActive = isActive;
        this.isVerified = isVerified;
        this.challengesCreated = challengesCreated;
        this.challengesCompleted = challengesCompleted;
        this.totalPoints = totalPoints;
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

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public Integer getChallengesCreated() { return challengesCreated; }
    public void setChallengesCreated(Integer challengesCreated) { this.challengesCreated = challengesCreated; }

    public Integer getChallengesCompleted() { return challengesCompleted; }
    public void setChallengesCompleted(Integer challengesCompleted) { this.challengesCompleted = challengesCompleted; }

    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }
}
