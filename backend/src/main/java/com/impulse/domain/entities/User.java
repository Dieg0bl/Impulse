package com.impulse.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * Entidad User - Usuario base del sistema
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email"),
    @Index(name = "idx_users_username", columnList = "username"),
    @Index(name = "idx_users_status", columnList = "status"),
    @Index(name = "idx_users_created_at", columnList = "createdAt")
})
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "Username es requerido")
    @Size(min = 3, max = 50, message = "Username debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username solo puede contener letras, números, guiones y guiones bajos")
    private String username;
    
    @Column(nullable = false, unique = true, length = 255)
    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe tener formato válido")
    private String email;
    
    @Column(nullable = false, length = 255)
    @NotBlank(message = "Password hash es requerido")
    private String passwordHash;
    
    @Column(length = 100)
    @Size(max = 100, message = "Nombre no puede exceder 100 caracteres")
    private String firstName;
    
    @Column(length = 100)
    @Size(max = 100, message = "Apellido no puede exceder 100 caracteres")
    private String lastName;
    
    @Column(length = 255)
    @Size(max = 255, message = "Avatar URL no puede exceder 255 caracteres")
    private String avatarUrl;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "Bio no puede exceder 1000 caracteres")
    private String bio;
    
    @Column(length = 100)
    @Size(max = 100, message = "Ubicación no puede exceder 100 caracteres")
    private String location;
    
    @Column(length = 255)
    @Size(max = 255, message = "Website URL no puede exceder 255 caracteres")
    private String websiteUrl;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;
    
    @Column(nullable = false)
    private Boolean emailVerified = false;
    
    @Column
    private LocalDateTime emailVerifiedAt;
    
    @Column
    private LocalDateTime lastLoginAt;
    
    @Column(nullable = false)
    @Min(value = 0, message = "Puntos no pueden ser negativos")
    private Integer totalPoints = 0;
    
    @Column(nullable = false)
    @Min(value = 1, message = "Nivel debe ser al menos 1")
    private Integer level = 1;
    
    @Column(nullable = false)
    @Min(value = 0, message = "Streak no puede ser negativo")
    private Integer currentStreak = 0;
    
    @Column(nullable = false)
    @Min(value = 0, message = "Longest streak no puede ser negativo")
    private Integer longestStreak = 0;
    
    @Column(nullable = false)
    @Min(value = 0, message = "Challenges completed no puede ser negativo")
    private Integer challengesCompleted = 0;
    
    @Column(nullable = false)
    @Min(value = 0, message = "Evidences submitted no puede ser negativo")
    private Integer evidencesSubmitted = 0;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        indexes = @Index(name = "idx_user_roles_user_id", columnList = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<UserRole> roles = new HashSet<>();
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "user_preferences",
        joinColumns = @JoinColumn(name = "user_id"),
        indexes = @Index(name = "idx_user_preferences_user_id", columnList = "user_id")
    )
    @MapKeyColumn(name = "preference_key", length = 50)
    @Column(name = "preference_value", length = 255)
    private Map<String, String> preferences = new HashMap<>();
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public User() {}
    
    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles.add(UserRole.USER);
    }
    
    // Business Methods
    
    /**
     * Verifica si el usuario tiene un rol específico
     */
    public boolean hasRole(UserRole role) {
        return roles.contains(role);
    }
    
    /**
     * Agrega un rol al usuario
     */
    public void addRole(UserRole role) {
        this.roles.add(role);
    }
    
    /**
     * Remueve un rol del usuario
     */
    public void removeRole(UserRole role) {
        this.roles.remove(role);
    }
    
    /**
     * Verifica si el usuario está activo
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
    
    /**
     * Marca el email como verificado
     */
    public void verifyEmail() {
        this.emailVerified = true;
        this.emailVerifiedAt = LocalDateTime.now();
    }
    
    /**
     * Actualiza el último login
     */
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }
    
    /**
     * Agrega puntos al usuario
     */
    public void addPoints(int points) {
        if (points > 0) {
            this.totalPoints += points;
            updateLevel();
        }
    }
    
    /**
     * Actualiza el nivel basado en puntos
     */
    private void updateLevel() {
        int newLevel = calculateLevelFromPoints(totalPoints);
        if (newLevel > level) {
            this.level = newLevel;
        }
    }
    
    /**
     * Calcula el nivel basado en puntos
     */
    private int calculateLevelFromPoints(int points) {
        // Formula: level = floor(sqrt(points / 100)) + 1
        return (int) Math.floor(Math.sqrt(points / 100.0)) + 1;
    }
    
    /**
     * Incrementa el streak actual
     */
    public void incrementStreak() {
        this.currentStreak++;
        if (currentStreak > longestStreak) {
            this.longestStreak = currentStreak;
        }
    }
    
    /**
     * Resetea el streak actual
     */
    public void resetStreak() {
        this.currentStreak = 0;
    }
    
    /**
     * Incrementa challenges completados
     */
    public void incrementChallengesCompleted() {
        this.challengesCompleted++;
    }
    
    /**
     * Incrementa evidencias enviadas
     */
    public void incrementEvidencesSubmitted() {
        this.evidencesSubmitted++;
    }
    
    /**
     * Obtiene el nombre completo
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else {
            return username;
        }
    }
    
    /**
     * Actualiza una preferencia
     */
    public void setPreference(String key, String value) {
        if (value != null) {
            preferences.put(key, value);
        } else {
            preferences.remove(key);
        }
    }
    
    /**
     * Obtiene una preferencia
     */
    public String getPreference(String key) {
        return preferences.get(key);
    }
    
    /**
     * Obtiene una preferencia con valor por defecto
     */
    public String getPreference(String key, String defaultValue) {
        return preferences.getOrDefault(key, defaultValue);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
    
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    
    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
    
    public LocalDateTime getEmailVerifiedAt() { return emailVerifiedAt; }
    public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) { this.emailVerifiedAt = emailVerifiedAt; }
    
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    
    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }
    
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    
    public Integer getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(Integer currentStreak) { this.currentStreak = currentStreak; }
    
    public Integer getLongestStreak() { return longestStreak; }
    public void setLongestStreak(Integer longestStreak) { this.longestStreak = longestStreak; }
    
    public Integer getChallengesCompleted() { return challengesCompleted; }
    public void setChallengesCompleted(Integer challengesCompleted) { this.challengesCompleted = challengesCompleted; }
    
    public Integer getEvidencesSubmitted() { return evidencesSubmitted; }
    public void setEvidencesSubmitted(Integer evidencesSubmitted) { this.evidencesSubmitted = evidencesSubmitted; }
    
    public Set<UserRole> getRoles() { return roles; }
    public void setRoles(Set<UserRole> roles) { this.roles = roles; }
    
    public Map<String, String> getPreferences() { return preferences; }
    public void setPreferences(Map<String, String> preferences) { this.preferences = preferences; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Enums
    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, BANNED
    }
    
    public enum UserRole {
        USER, COACH, VALIDATOR, MODERATOR, ADMIN
    }
}
