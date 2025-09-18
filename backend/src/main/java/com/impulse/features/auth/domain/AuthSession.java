package com.impulse.features.auth.domain;

import com.impulse.shared.error.DomainException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity: AuthSession
 * Business logic for user authentication sessions - tracking active sessions
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public class AuthSession {
    private final AuthSessionId id;
    private final Long userId;
    private final String userAgent;
    private final String ipAddress;
    private final LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;
    private LocalDateTime expiresAt;
    private boolean isActive;
    private String refreshTokenId; // Link to current refresh token

    // Constructor for creation
    public AuthSession(AuthSessionId id, Long userId, String userAgent,
                      String ipAddress, LocalDateTime expiresAt) {
        this.id = Objects.requireNonNull(id, "AuthSession ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        this.createdAt = LocalDateTime.now();
        this.lastAccessedAt = this.createdAt;
        this.expiresAt = Objects.requireNonNull(expiresAt, "Expires at cannot be null");
        this.isActive = true;

        validateExpiration();
    }

    // Constructor for reconstruction
    public AuthSession(AuthSessionId id, Long userId, String userAgent, String ipAddress,
                      LocalDateTime createdAt, LocalDateTime lastAccessedAt,
                      LocalDateTime expiresAt, boolean isActive, String refreshTokenId) {
        this.id = id;
        this.userId = userId;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
        this.lastAccessedAt = lastAccessedAt;
        this.expiresAt = expiresAt;
        this.isActive = isActive;
        this.refreshTokenId = refreshTokenId;
    }

    // Factory method
    public static AuthSession create(Long userId, String userAgent, String ipAddress,
                                   LocalDateTime expiresAt) {
        return new AuthSession(
            AuthSessionId.generate(),
            userId,
            userAgent,
            ipAddress,
            expiresAt
        );
    }

    // Business methods
    public boolean isValidSession() {
        return isActive && !isExpired();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void updateLastAccessed() {
        if (!isActive) {
            throw new DomainException("Cannot update access time for inactive session");
        }
        if (isExpired()) {
            throw new DomainException("Cannot update access time for expired session");
        }

        this.lastAccessedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void linkRefreshToken(String refreshTokenId) {
        if (!isActive) {
            throw new DomainException("Cannot link refresh token to inactive session");
        }
        this.refreshTokenId = refreshTokenId;
    }

    public void unlinkRefreshToken() {
        this.refreshTokenId = null;
    }

    public boolean hasRefreshToken() {
        return refreshTokenId != null;
    }

    public void extendExpiration(LocalDateTime newExpiresAt) {
        if (!isActive) {
            throw new DomainException("Cannot extend expiration for inactive session");
        }
        if (newExpiresAt.isBefore(LocalDateTime.now())) {
            throw new DomainException("New expiration time cannot be in the past");
        }

        this.expiresAt = newExpiresAt;
    }

    public boolean canBeExtended() {
        return isActive && !isExpired();
    }

    public boolean belongsToUser(Long userId) {
        return Objects.equals(this.userId, userId);
    }

    public boolean matchesContext(String userAgent, String ipAddress) {
        // For security validation - checking if request context matches session
        return Objects.equals(this.userAgent, userAgent) &&
               Objects.equals(this.ipAddress, ipAddress);
    }

    // Private validation methods
    private void validateExpiration() {
        if (expiresAt.isBefore(createdAt) || expiresAt.isEqual(createdAt)) {
            throw new DomainException("Expiration time must be after creation time");
        }

        // Maximum 30 days session validity
        if (expiresAt.isAfter(createdAt.plusDays(30))) {
            throw new DomainException("Session cannot be valid for more than 30 days");
        }
    }

    // Getters
    public AuthSessionId getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUserAgent() { return userAgent; }
    public String getIpAddress() { return ipAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastAccessedAt() { return lastAccessedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isActive() { return isActive; }
    public String getRefreshTokenId() { return refreshTokenId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthSession that = (AuthSession) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
