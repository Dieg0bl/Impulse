package com.impulse.domain.user;

import java.util.Objects;
import java.util.UUID;

/**
 * UserId - Value Object para identificadores de usuario
 */
public class UserId {

    private final String value;

    private UserId(String value) {
        this.value = Objects.requireNonNull(value, "UserId value cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId value cannot be empty");
        }
    }

    public static UserId of(String value) {
        return new UserId(value);
    }

    public static UserId of(Long value) {
        return new UserId(value.toString());
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    public Long asLong() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("UserId is not a valid Long: " + value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "UserId{" + value + '}';
    }
}
