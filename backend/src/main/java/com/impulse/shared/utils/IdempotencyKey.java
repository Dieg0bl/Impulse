package com.impulse.shared.utils;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object: IdempotencyKey
 * Represents an idempotency key for request deduplication
 */
public class IdempotencyKey {
    private final String value;

    private IdempotencyKey(String value) {
        this.value = Objects.requireNonNull(value, "Idempotency key value cannot be null");
    }

    public static IdempotencyKey of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Idempotency key cannot be null or empty");
        }
        return new IdempotencyKey(value.trim());
    }

    public static IdempotencyKey generate() {
        return new IdempotencyKey(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdempotencyKey that = (IdempotencyKey) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
