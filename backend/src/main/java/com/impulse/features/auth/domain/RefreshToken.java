package com.impulse.features.auth.domain;

import com.impulse.shared.error.DomainException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Domain entity: RefreshToken
 * Business logic for refresh token lifecycle and rotation
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public class RefreshToken {
    private final RefreshTokenId id;
    private final Long userId;
    private final String tokenHash; // SHA-256 hash of actual token
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;
    private LocalDateTime revokedAt;
    private RefreshTokenId replacedBy;
    private final String userAgent;
    private final String ipAddress;

    // Constructor for creation
    public RefreshToken(RefreshTokenId id, Long userId, String tokenHash,
                       LocalDateTime createdAt, LocalDateTime expiresAt,
                       String userAgent, String ipAddress) {
        this.id = Objects.requireNonNull(id, "RefreshToken ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.tokenHash = Objects.requireNonNull(tokenHash, "Token hash cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "Expires at cannot be null");
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;

        validateTokenHash(tokenHash);
        validateExpiration(createdAt, expiresAt);
    }

    // Constructor for reconstruction
    public RefreshToken(RefreshTokenId id, Long userId, String tokenHash,
                       LocalDateTime createdAt, LocalDateTime expiresAt,
                       LocalDateTime revokedAt, RefreshTokenId replacedBy,
                       String userAgent, String ipAddress) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
        this.replacedBy = replacedBy;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }

    // Factory method
    public static RefreshToken create(Long userId, String rawToken,
                                    LocalDateTime expiresAt, String userAgent, String ipAddress) {
        return new RefreshToken(
            RefreshTokenId.generate(),
            userId,
            hashToken(rawToken),
            LocalDateTime.now(),
            expiresAt,
            userAgent,
            ipAddress
        );
    }

    // Business methods
    public boolean isValid() {
        return !isRevoked() && !isExpired();
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean wasReplaced() {
        return replacedBy != null;
    }

    public void revoke() {
        if (isRevoked()) {
            throw new DomainException("Token is already revoked");
        }
        this.revokedAt = LocalDateTime.now();
    }

    public void markAsReplaced(RefreshTokenId newTokenId) {
        if (newTokenId == null) {
            throw new DomainException("Replacement token ID cannot be null");
        }
        if (isRevoked()) {
            throw new DomainException("Cannot replace an already revoked token");
        }

        this.replacedBy = newTokenId;
        this.revokedAt = LocalDateTime.now();
    }

    public boolean matchesToken(String rawToken) {
        if (rawToken == null) {
            return false;
        }
        return this.tokenHash.equals(hashToken(rawToken));
    }

    // Static utility methods
    public static String hashToken(String rawToken) {
        if (rawToken == null || rawToken.trim().isEmpty()) {
            throw new DomainException("Token cannot be null or empty");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new DomainException("SHA-256 algorithm not available");
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
    }

    // Private validation methods
    private void validateTokenHash(String tokenHash) {
        if (tokenHash.length() != 64) { // SHA-256 hex string length
            throw new DomainException("Invalid token hash length");
        }
    }

    private void validateExpiration(LocalDateTime createdAt, LocalDateTime expiresAt) {
        if (expiresAt.isBefore(createdAt) || expiresAt.isEqual(createdAt)) {
            throw new DomainException("Expiration time must be after creation time");
        }
    }

    // Getters
    public RefreshTokenId getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTokenHash() { return tokenHash; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getRevokedAt() { return revokedAt; }
    public RefreshTokenId getReplacedBy() { return replacedBy; }
    public String getUserAgent() { return userAgent; }
    public String getIpAddress() { return ipAddress; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
