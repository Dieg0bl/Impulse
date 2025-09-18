package com.impulse.features.auth.domain;

import com.impulse.shared.error.DomainException;
import java.util.Objects;
import java.util.UUID;

/**
 * Value object: RefreshTokenId
 * Anexo 1 - IMPULSE v1.0 specification compliant
 */
public class RefreshTokenId {
    private final UUID value;

    private RefreshTokenId(UUID value) {
        this.value = Objects.requireNonNull(value, "RefreshTokenId value cannot be null");
    }

    public static RefreshTokenId of(UUID value) {
        return new RefreshTokenId(value);
    }

    public static RefreshTokenId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("RefreshTokenId string value cannot be null or empty");
        }

        try {
            return new RefreshTokenId(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException e) {
            throw new DomainException("Invalid RefreshTokenId format: " + value);
        }
    }

    public static RefreshTokenId generate() {
        return new RefreshTokenId(UUID.randomUUID());
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
        RefreshTokenId that = (RefreshTokenId) o;
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
