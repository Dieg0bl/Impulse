package com.impulse.features.auth.application.dto;

import java.util.Objects;

/**
 * Response DTO for user registration
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public class RegisterUserResponse {
    private final Long userId;
    private final String username;
    private final String email;
    private final boolean emailVerificationRequired;
    private final String message;

    public RegisterUserResponse(Long userId, String username, String email,
                              boolean emailVerificationRequired, String message) {
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.emailVerificationRequired = emailVerificationRequired;
        this.message = message;
    }

    // Getters
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public boolean isEmailVerificationRequired() { return emailVerificationRequired; }
    public String getMessage() { return message; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterUserResponse that = (RegisterUserResponse) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
