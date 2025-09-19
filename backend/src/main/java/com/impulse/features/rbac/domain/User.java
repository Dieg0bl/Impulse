package com.impulse.features.rbac.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import com.impulse.shared.annotations.Generated;

/**
 * Domain entity: User
 * Represents a user in the system with roles and permissions
 */
@Generated
public class User {
    private final Long id;
    private final String username;
    private final String email;
    private final String displayName;
    private final String passwordHash;
    private final String role; // e.g. "USER", "ADMIN", "TUTOR"
    private final boolean emailVerified;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // Flags para datos sintéticos/plantillas
    private final boolean isDemo;
    private final boolean isOfficial;
    private final boolean isBot;

    public User(Long id, String username, String email, String displayName, String passwordHash, String role,
                boolean emailVerified, LocalDateTime createdAt, LocalDateTime updatedAt,
                boolean isDemo, boolean isOfficial, boolean isBot) {
        this.id = Objects.requireNonNull(id, "User ID cannot be null");
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.role = Objects.requireNonNull(role, "Role cannot be null");
        this.emailVerified = emailVerified;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = updatedAt;
        this.isDemo = isDemo;
        this.isOfficial = isOfficial;
        this.isBot = isBot;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public boolean isEmailVerified() { return emailVerified; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public boolean isDemo() { return isDemo; }
    public boolean isOfficial() { return isOfficial; }
    public boolean isBot() { return isBot; }

    // Métodos de dominio relevantes (puedes expandir según reglas de negocio)
    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(role); }
    public boolean isTutor() { return "TUTOR".equalsIgnoreCase(role); }
    public boolean isUser() { return "USER".equalsIgnoreCase(role); }
}
