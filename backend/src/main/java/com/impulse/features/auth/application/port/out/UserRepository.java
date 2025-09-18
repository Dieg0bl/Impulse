package com.impulse.features.auth.application.port.out;

import java.util.Optional;

/**
 * Outbound port for user persistence
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public interface UserRepository {
    /**
     * User representation for auth module
     */
    public static class User {
        private final Long id;
        private final String username;
        private final String email;
        private final String passwordHash;
        private final String firstName;
        private final String lastName;
        private final boolean emailVerified;
        private final boolean isActive;

        public User(Long id, String username, String email, String passwordHash,
                   String firstName, String lastName, boolean emailVerified, boolean isActive) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.passwordHash = passwordHash;
            this.firstName = firstName;
            this.lastName = lastName;
            this.emailVerified = emailVerified;
            this.isActive = isActive;
        }

        // Getters
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPasswordHash() { return passwordHash; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public boolean isEmailVerified() { return emailVerified; }
        public boolean isActive() { return isActive; }
    }

    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void updateEmailVerified(Long userId, boolean verified);
    void updatePassword(Long userId, String passwordHash);
}
