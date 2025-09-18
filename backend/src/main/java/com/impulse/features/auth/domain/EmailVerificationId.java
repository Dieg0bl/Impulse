package com.impulse.features.auth.domain;

import com.impulse.shared.error.DomainException;
import java.util.Objects;
import java.util.UUID;

/**
 * Value object: EmailVerificationId
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public class EmailVerificationId {
    private final UUID value;

    private EmailVerificationId(UUID value) {
        this.value = Objects.requireNonNull(value, "EmailVerificationId value cannot be null");
    }

    public static EmailVerificationId of(UUID value) {
        return new EmailVerificationId(value);
    }

    public static EmailVerificationId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("EmailVerificationId string value cannot be null or empty");
        }

        try {
            return new EmailVerificationId(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException e) {
            throw new DomainException("Invalid EmailVerificationId format: " + value);
        }
    }

    public static EmailVerificationId generate() {
        return new EmailVerificationId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    public String asString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailVerificationId that = (EmailVerificationId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
