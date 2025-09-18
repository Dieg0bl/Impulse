package com.impulse.features.auth.domain;

import com.impulse.shared.error.DomainException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Domain entity: PasswordReset
 * Business logic for password reset tokens - one-time use
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public class PasswordReset {
    private final PasswordResetId id;
    private final Long userId;
    private final String tokenHash; // SHA-256 hash of actual token
    private final LocalDateTime expiresAt;
    private LocalDateTime usedAt;
    private final String userAgent;
    private final String ipAddress;
    private final LocalDateTime createdAt;

    // Constructor for creation
    public PasswordReset(PasswordResetId id, Long userId, String tokenHash,
                        LocalDateTime expiresAt, String userAgent, String ipAddress) {
        this.id = Objects.requireNonNull(id, "PasswordReset ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.tokenHash = Objects.requireNonNull(tokenHash, "Token hash cannot be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "Expires at cannot be null");
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        this.createdAt = LocalDateTime.now();

        validateTokenHash(tokenHash);
        validateExpiration();
    }

    // Constructor for reconstruction
    public PasswordReset(PasswordResetId id, Long userId, String tokenHash,
                        LocalDateTime expiresAt, LocalDateTime usedAt,
                        String userAgent, String ipAddress, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
    }

    // Factory method
    public static PasswordReset create(Long userId, String rawToken,
                                     LocalDateTime expiresAt, String userAgent, String ipAddress) {
        return new PasswordReset(
            PasswordResetId.generate(),
            userId,
            hashToken(rawToken),
            expiresAt,
            userAgent,
            ipAddress
        );
    }

    // Business methods
    public boolean isValid() {
        return !isUsed() && !isExpired();
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void markAsUsed() {
        if (isUsed()) {
            throw new DomainException("Password reset token is already used");
        }
        if (isExpired()) {
            throw new DomainException("Password reset token has expired");
        }

        this.usedAt = LocalDateTime.now();
    }

    public boolean matchesToken(String rawToken) {
        if (rawToken == null) {
            return false;
        }
        return this.tokenHash.equals(hashToken(rawToken));
    }

    public boolean canBeUsed() {
        return isValid();
    }

    // Static utility methods
    public static String hashToken(String rawToken) {
        if (rawToken == null || rawToken.trim().isEmpty()) {
            throw new DomainException("Token cannot be null or empty");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));

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
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 256 bits
        random.nextBytes(bytes);

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Private validation methods
    private void validateTokenHash(String tokenHash) {
        if (tokenHash.length() != 64) { // SHA-256 hex string length
            throw new DomainException("Invalid token hash length");
        }
    }

    private void validateExpiration() {
        if (expiresAt.isBefore(createdAt) || expiresAt.isEqual(createdAt)) {
            throw new DomainException("Expiration time must be after creation time");
        }

        // Maximum 24 hours validity
        if (expiresAt.isAfter(createdAt.plusHours(24))) {
            throw new DomainException("Password reset token cannot be valid for more than 24 hours");
        }
    }

    // Getters
    public PasswordResetId getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTokenHash() { return tokenHash; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getUsedAt() { return usedAt; }
    public String getUserAgent() { return userAgent; }
    public String getIpAddress() { return ipAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordReset that = (PasswordReset) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
