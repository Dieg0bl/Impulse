package com.impulse.interfaces.rest.dto;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO para respuesta de usuario (sin información sensible)
 */
public class UserResponseDto {
    
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String status;
    private String role;
    private Integer level;
    private Long totalPoints;
    private Integer streakDays;
    private String profilePictureUrl;
    private String bio;
    private String website;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private Set<String> badges;
    private Integer challengesCompleted;
    private Integer evidenceSubmitted;
    private String preferredLanguage;
    private String timeZone;
    private Boolean emailVerified;
    private Boolean profilePublic;
    
    // Constructors
    public UserResponseDto() {}
    
    public UserResponseDto(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    
    public Long getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Long totalPoints) { this.totalPoints = totalPoints; }
    
    public Integer getStreakDays() { return streakDays; }
    public void setStreakDays(Integer streakDays) { this.streakDays = streakDays; }
    
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    
    public Set<String> getBadges() { return badges; }
    public void setBadges(Set<String> badges) { this.badges = badges; }
    
    public Integer getChallengesCompleted() { return challengesCompleted; }
    public void setChallengesCompleted(Integer challengesCompleted) { this.challengesCompleted = challengesCompleted; }
    
    public Integer getEvidenceSubmitted() { return evidenceSubmitted; }
    public void setEvidenceSubmitted(Integer evidenceSubmitted) { this.evidenceSubmitted = evidenceSubmitted; }
    
    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }
    
    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }
    
    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
    
    public Boolean getProfilePublic() { return profilePublic; }
    public void setProfilePublic(Boolean profilePublic) { this.profilePublic = profilePublic; }
}
