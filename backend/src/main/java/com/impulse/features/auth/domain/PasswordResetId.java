package com.impulse.features.auth.domain;

import com.impulse.shared.error.DomainException;
import java.util.Objects;
import java.util.UUID;

/**
 * Value object: PasswordResetId
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public class PasswordResetId {
    private final UUID value;

    private PasswordResetId(UUID value) {
        this.value = Objects.requireNonNull(value, "PasswordResetId value cannot be null");
    }

    public static PasswordResetId of(UUID value) {
        return new PasswordResetId(value);
    }

    public static PasswordResetId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("PasswordResetId string value cannot be null or empty");
        }

        try {
            return new PasswordResetId(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException e) {
            throw new DomainException("Invalid PasswordResetId format: " + value);
        }
    }

    public static PasswordResetId generate() {
        return new PasswordResetId(UUID.randomUUID());
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
        PasswordResetId that = (PasswordResetId) o;
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
