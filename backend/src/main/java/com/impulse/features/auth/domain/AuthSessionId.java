package com.impulse.features.auth.domain;

import com.impulse.shared.error.DomainException;
import java.util.Objects;
import java.util.UUID;

/**
 * Value object: AuthSessionId
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public class AuthSessionId {
    private final UUID value;

    private AuthSessionId(UUID value) {
        this.value = Objects.requireNonNull(value, "AuthSessionId value cannot be null");
    }

    public static AuthSessionId of(UUID value) {
        return new AuthSessionId(value);
    }

    public static AuthSessionId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("AuthSessionId string value cannot be null or empty");
        }

        try {
            return new AuthSessionId(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException e) {
            throw new DomainException("Invalid AuthSessionId format: " + value);
        }
    }

    public static AuthSessionId generate() {
        return new AuthSessionId(UUID.randomUUID());
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
        AuthSessionId that = (AuthSessionId) o;
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
