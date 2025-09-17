package com.impulse.application.user.dto;

import java.time.LocalDateTime;

import com.impulse.domain.enums.UserRole;
import com.impulse.domain.enums.UserStatus;

/**
 * UserResponse - DTO de respuesta para usuarios
 */
public class UserResponse {

    private final String id;
    private final String email;
    private final String name;
    private final UserRole role;
    private final UserStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastLoginAt;
    private final boolean emailVerified;

    public UserResponse(String id, String email, String name, UserRole role,
                       UserStatus status, LocalDateTime createdAt,
                       LocalDateTime lastLoginAt, boolean emailVerified) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
        this.emailVerified = emailVerified;
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public boolean isEmailVerified() { return emailVerified; }
}
