package com.impulse.domain.user;

import com.impulse.domain.enums.UserRole;
import com.impulse.domain.enums.UserStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * User - Entidad de dominio que representa a un usuario del sistema
 *
 * SUPERVERSIÓN FUSIONADA que combina:
 * - Campos completos de la versión JPA (firstName, lastName, phoneNumber, etc.)
 * - Value objects y métodos de dominio de la versión Clean Architecture
 * - Validaciones robustas y factory methods
 * - Builder pattern para construcción flexible
 * - Inmutabilidad con métodos de copia para cambios de estado
 */
public class User {

    // Identificador único del usuario
    private final UserId id;

    // Información básica
    private final Email email;
    private final String firstName;
    private final String lastName;
    private final String password;
    private final String phoneNumber;
    private final String profileImageUrl;

    // Roles y estado
    private final UserRole role;
    private final UserStatus status;

    // Verificación y control
    private final boolean emailVerified;

    // Fechas de control
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime lastLoginAt;

    // Preferencias del usuario
    private final List<String> preferences;

    // Constructor completo
    private User(UserId id, Email email, String firstName, String lastName,
                String password, String phoneNumber, String profileImageUrl,
                UserRole role, UserStatus status, boolean emailVerified,
                LocalDateTime createdAt, LocalDateTime updatedAt,
                LocalDateTime lastLoginAt, List<String> preferences) {
        this.id = Objects.requireNonNull(id, "User ID cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.firstName = validateAndTrim(firstName, "First name");
        this.lastName = validateAndTrim(lastName, "Last name");
        this.password = Objects.requireNonNull(password, "Password cannot be null");
        this.phoneNumber = phoneNumber != null ? phoneNumber.trim() : null;
        this.profileImageUrl = profileImageUrl != null ? profileImageUrl.trim() : null;
        this.role = Objects.requireNonNull(role, "Role cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.emailVerified = emailVerified;
        this.createdAt = Objects.requireNonNull(createdAt, "Created date cannot be null");
        this.updatedAt = updatedAt;
        this.lastLoginAt = lastLoginAt;
        this.preferences = preferences != null ? List.copyOf(preferences) : List.of();
    }

    // Factory method para crear nuevo usuario básico
    public static User create(Email email, String firstName, String lastName,
                            String password, UserRole role) {
        return new Builder()
            .withId(UserId.generate())
            .withEmail(email)
            .withFirstName(firstName)
            .withLastName(lastName)
            .withPassword(password)
            .withRole(role)
            .withStatus(UserStatus.PENDING_VERIFICATION)
            .withEmailVerified(false)
            .withCreatedAt(LocalDateTime.now())
            .build();
    }

    // Factory method para crear usuario completo
    public static User createComplete(Email email, String firstName, String lastName,
                                    String password, String phoneNumber, UserRole role) {
        return new Builder()
            .withId(UserId.generate())
            .withEmail(email)
            .withFirstName(firstName)
            .withLastName(lastName)
            .withPassword(password)
            .withPhoneNumber(phoneNumber)
            .withRole(role)
            .withStatus(UserStatus.PENDING_VERIFICATION)
            .withEmailVerified(false)
            .withCreatedAt(LocalDateTime.now())
            .build();
    }

    // Factory method para reconstruir desde persistencia (con builder interno para evitar muchos parámetros)
    public static User reconstruct(UserReconstructionData data) {
        return new Builder()
            .withId(data.getId())
            .withEmail(data.getEmail())
            .withFirstName(data.getFirstName())
            .withLastName(data.getLastName())
            .withPassword(data.getPassword())
            .withPhoneNumber(data.getPhoneNumber())
            .withProfileImageUrl(data.getProfileImageUrl())
            .withRole(data.getRole())
            .withStatus(data.getStatus())
            .withEmailVerified(data.isEmailVerified())
            .withCreatedAt(data.getCreatedAt())
            .withUpdatedAt(data.getUpdatedAt())
            .withLastLoginAt(data.getLastLoginAt())
            .withPreferences(data.getPreferences())
            .build();
    }

    // Clase helper para la reconstrucción desde persistencia
    public static class UserReconstructionData {
        private final UserId id;
        private final Email email;
        private final String firstName;
        private final String lastName;
        private final String password;
        private final String phoneNumber;
        private final String profileImageUrl;
        private final UserRole role;
        private final UserStatus status;
        private final boolean emailVerified;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;
        private final LocalDateTime lastLoginAt;
        private final List<String> preferences;

        public UserReconstructionData(UserId id, Email email, String firstName, String lastName,
                                    String password, String phoneNumber, String profileImageUrl,
                                    UserRole role, UserStatus status, boolean emailVerified,
                                    LocalDateTime createdAt, LocalDateTime updatedAt,
                                    LocalDateTime lastLoginAt, List<String> preferences) {
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.password = password;
            this.phoneNumber = phoneNumber;
            this.profileImageUrl = profileImageUrl;
            this.role = role;
            this.status = status;
            this.emailVerified = emailVerified;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.lastLoginAt = lastLoginAt;
            this.preferences = preferences;
        }

        // Getters
        public UserId getId() { return id; }
        public Email getEmail() { return email; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPassword() { return password; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getProfileImageUrl() { return profileImageUrl; }
        public UserRole getRole() { return role; }
        public UserStatus getStatus() { return status; }
        public boolean isEmailVerified() { return emailVerified; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public LocalDateTime getLastLoginAt() { return lastLoginAt; }
        public List<String> getPreferences() { return preferences; }
    }

    // Métodos de dominio para gestión del estado (retornan nuevas instancias)
    public User activate() {
        if (this.status == UserStatus.SUSPENDED) {
            throw new UserDomainError("Cannot activate suspended user");
        }
        return new Builder()
            .fromUser(this)
            .withStatus(UserStatus.ACTIVE)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public User deactivate() {
        return new Builder()
            .fromUser(this)
            .withStatus(UserStatus.INACTIVE)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public User suspend() {
        return new Builder()
            .fromUser(this)
            .withStatus(UserStatus.SUSPENDED)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public User verifyEmail() {
        return new Builder()
            .fromUser(this)
            .withEmailVerified(true)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public User updateLastLogin() {
        return new Builder()
            .fromUser(this)
            .withLastLoginAt(LocalDateTime.now())
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public User changePassword(String newPassword) {
        Objects.requireNonNull(newPassword, "New password cannot be null");
        if (newPassword.trim().isEmpty()) {
            throw new UserDomainError("Password cannot be empty");
        }
        return new Builder()
            .fromUser(this)
            .withPassword(newPassword)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public User updateProfile(String firstName, String lastName, String phoneNumber) {
        return new Builder()
            .fromUser(this)
            .withFirstName(firstName)
            .withLastName(lastName)
            .withPhoneNumber(phoneNumber)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public User updateProfileImage(String profileImageUrl) {
        return new Builder()
            .fromUser(this)
            .withProfileImageUrl(profileImageUrl)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public User updatePreferences(List<String> preferences) {
        return new Builder()
            .fromUser(this)
            .withPreferences(preferences)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    public User changeRole(UserRole newRole) {
        return new Builder()
            .fromUser(this)
            .withRole(newRole)
            .withUpdatedAt(LocalDateTime.now())
            .build();
    }

    // Métodos de consulta del dominio
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public boolean isEmailVerified() {
        return this.emailVerified;
    }

    public boolean canLogin() {
        return isActive() && isEmailVerified();
    }

    public boolean hasRole(UserRole role) {
        return this.role == role;
    }

    public String getFullName() {
        if (firstName == null && lastName == null) {
            return email.getValue();
        }
        StringBuilder fullName = new StringBuilder();
        if (firstName != null && !firstName.trim().isEmpty()) {
            fullName.append(firstName.trim());
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            if (fullName.length() > 0) {
                fullName.append(" ");
            }
            fullName.append(lastName.trim());
        }
        return fullName.length() > 0 ? fullName.toString() : email.getValue();
    }

    public String getDisplayName() {
        return getFullName();
    }

    public boolean hasProfileImage() {
        return profileImageUrl != null && !profileImageUrl.trim().isEmpty();
    }

    public boolean hasPhoneNumber() {
        return phoneNumber != null && !phoneNumber.trim().isEmpty();
    }

    public boolean hasPreferences() {
        return preferences != null && !preferences.isEmpty();
    }

    public boolean isPendingVerification() {
        return this.status == UserStatus.PENDING_VERIFICATION;
    }

    public boolean isSuspended() {
        return this.status == UserStatus.SUSPENDED;
    }

    // Getters
    public UserId getId() { return id; }
    public Email getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPassword() { return password; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public boolean getEmailVerified() { return emailVerified; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public List<String> getPreferences() { return new ArrayList<>(preferences); }

    // Métodos de compatibilidad para persistencia JPA
    public String getEmailValue() { return email.getValue(); }
    public String getIdValue() { return id.getValue(); }
    public Long getIdAsLong() { return id.asLong(); }

    // Métodos de compatibilidad legacy
    public String getName() { return getFullName(); }
    public String getUsername() { return email.getValue(); }

    // Validación helper
    private String validateAndTrim(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        return trimmed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email=" + email +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", emailVerified=" + emailVerified +
                '}';
    }

    // Builder Pattern para construcción flexible
    public static class Builder {
        private UserId id;
        private Email email;
        private String firstName;
        private String lastName;
        private String password;
        private String phoneNumber;
        private String profileImageUrl;
        private UserRole role;
        private UserStatus status;
        private boolean emailVerified;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime lastLoginAt;
        private List<String> preferences;

        public Builder() {
            // Constructor vacío para inicialización del builder
        }

        public Builder fromUser(User user) {
            this.id = user.id;
            this.email = user.email;
            this.firstName = user.firstName;
            this.lastName = user.lastName;
            this.password = user.password;
            this.phoneNumber = user.phoneNumber;
            this.profileImageUrl = user.profileImageUrl;
            this.role = user.role;
            this.status = user.status;
            this.emailVerified = user.emailVerified;
            this.createdAt = user.createdAt;
            this.updatedAt = user.updatedAt;
            this.lastLoginAt = user.lastLoginAt;
            this.preferences = new ArrayList<>(user.preferences);
            return this;
        }

        public Builder withId(UserId id) {
            this.id = id;
            return this;
        }

        public Builder withEmail(Email email) {
            this.email = email;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder withProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
            return this;
        }

        public Builder withRole(UserRole role) {
            this.role = role;
            return this;
        }

        public Builder withStatus(UserStatus status) {
            this.status = status;
            return this;
        }

        public Builder withEmailVerified(boolean emailVerified) {
            this.emailVerified = emailVerified;
            return this;
        }

        public Builder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder withLastLoginAt(LocalDateTime lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
            return this;
        }

        public Builder withPreferences(List<String> preferences) {
            this.preferences = preferences;
            return this;
        }

        public User build() {
            return new User(id, email, firstName, lastName, password, phoneNumber,
                          profileImageUrl, role, status, emailVerified, createdAt,
                          updatedAt, lastLoginAt, preferences);
        }
    }
}
