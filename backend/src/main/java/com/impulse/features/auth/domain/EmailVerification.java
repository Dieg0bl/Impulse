package com.impulse.features.auth.domain;

import com.impulse.shared.error.DomainException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Domain entity: EmailVerification
 * Business logic for email verification tokens - required for social functions
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public class EmailVerification {
    private final EmailVerificationId id;
    private final Long userId;
    private final String email;
    private final String tokenHash; // SHA-256 hash of actual token
    private final LocalDateTime expiresAt;
    private LocalDateTime verifiedAt;
    private final String userAgent;
    private final String ipAddress;
    private final LocalDateTime createdAt;

    // Constructor for creation
    public EmailVerification(EmailVerificationId id, Long userId, String email,
                           String tokenHash, LocalDateTime expiresAt,
                           String userAgent, String ipAddress) {
        this.id = Objects.requireNonNull(id, "EmailVerification ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.tokenHash = Objects.requireNonNull(tokenHash, "Token hash cannot be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "Expires at cannot be null");
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        this.createdAt = LocalDateTime.now();

        validateEmail();
        validateTokenHash(tokenHash);
        validateExpiration();
    }

    // Constructor for reconstruction
    public EmailVerification(EmailVerificationId id, Long userId, String email,
                           String tokenHash, LocalDateTime expiresAt, LocalDateTime verifiedAt,
                           String userAgent, String ipAddress, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.verifiedAt = verifiedAt;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
    }

    // Factory method
    public static EmailVerification create(Long userId, String email, String rawToken,
                                         LocalDateTime expiresAt, String userAgent, String ipAddress) {
        return new EmailVerification(
            EmailVerificationId.generate(),
            userId,
            email,
            hashToken(rawToken),
            expiresAt,
            userAgent,
            ipAddress
        );
    }

    // Business methods
    public boolean isValid() {
        return !isVerified() && !isExpired();
    }

    public boolean isVerified() {
        return verifiedAt != null;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void markAsVerified() {
        if (isVerified()) {
            throw new DomainException("Email is already verified");
        }
        if (isExpired()) {
            throw new DomainException("Email verification token has expired");
        }

        this.verifiedAt = LocalDateTime.now();
    }

    public boolean matchesToken(String rawToken) {
        if (rawToken == null) {
            return false;
        }
        return this.tokenHash.equals(hashToken(rawToken));
    }

    public boolean canBeVerified() {
        return isValid();
    }

    public boolean isEmailVerificationRequired() {
        // As per Anexo 1: email verification required for social functions
        return !isVerified();
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
    private void validateEmail() {
        if (email.trim().isEmpty()) {
            throw new DomainException("Email cannot be empty");
        }

        // Basic email validation
        if (!email.contains("@") || !email.contains(".")) {
            throw new DomainException("Invalid email format");
        }
    }

    private void validateTokenHash(String tokenHash) {
        if (tokenHash.length() != 64) { // SHA-256 hex string length
            throw new DomainException("Invalid token hash length");
        }
    }

    private void validateExpiration() {
        if (expiresAt.isBefore(createdAt) || expiresAt.isEqual(createdAt)) {
            throw new DomainException("Expiration time must be after creation time");
        }

        // Maximum 72 hours validity for email verification
        if (expiresAt.isAfter(createdAt.plusHours(72))) {
            throw new DomainException("Email verification token cannot be valid for more than 72 hours");
        }
    }

    // Getters
    public EmailVerificationId getId() { return id; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getTokenHash() { return tokenHash; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public String getUserAgent() { return userAgent; }
    public String getIpAddress() { return ipAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailVerification that = (EmailVerification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
