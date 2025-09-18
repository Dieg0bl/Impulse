package com.impulse.features.auth.application.dto;

import java.util.Objects;

/**
 * Response DTO for user login
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public class LoginUserResponse {
    private final Long userId;
    private final String username;
    private final String accessToken;
    private final String refreshToken;
    private final String sessionId;
    private final boolean emailVerified;
    private final long expiresIn; // Access token expiration in seconds

    public LoginUserResponse(Long userId, String username, String accessToken,
                           String refreshToken, String sessionId, boolean emailVerified,
                           long expiresIn) {
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.accessToken = Objects.requireNonNull(accessToken, "Access token cannot be null");
        this.refreshToken = Objects.requireNonNull(refreshToken, "Refresh token cannot be null");
        this.sessionId = Objects.requireNonNull(sessionId, "Session ID cannot be null");
        this.emailVerified = emailVerified;
        this.expiresIn = expiresIn;
    }

    // Getters
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getSessionId() { return sessionId; }
    public boolean isEmailVerified() { return emailVerified; }
    public long getExpiresIn() { return expiresIn; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginUserResponse that = (LoginUserResponse) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, sessionId);
    }
}
