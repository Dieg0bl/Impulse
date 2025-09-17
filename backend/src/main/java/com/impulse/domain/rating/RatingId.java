package com.impulse.domain.rating;

import java.util.Objects;
import java.util.UUID;

/**
 * RatingId - Value Object para identificadores de valoración
 */
public class RatingId {

    private final String value;

    private RatingId(String value) {
        this.value = Objects.requireNonNull(value, "RatingId value cannot be null");
    }

    private RatingId(Long value) {
        this.value = Objects.requireNonNull(value, "RatingId value cannot be null").toString();
    }

    public static RatingId of(Long value) {
        return new RatingId(value);
    }

    public static RatingId of(String value) {
        return new RatingId(value);
    }

    public static RatingId generate() {
        return new RatingId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    public Long asLong() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("RatingId is not a valid Long: " + value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RatingId that = (RatingId) o;
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
