package com.impulse.features.challenge.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object: ChallengeId
 * Represents a unique identifier for a Challenge
 */
public class ChallengeId {
    private final String value;

    private ChallengeId(String value) {
        this.value = Objects.requireNonNull(value, "Challenge ID value cannot be null");
    }

    public static ChallengeId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Challenge ID cannot be null or empty");
        }
        return new ChallengeId(value.trim());
    }

    public static ChallengeId of(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Challenge ID must be a positive number");
        }
        return new ChallengeId(id.toString());
    }

    public static ChallengeId generate() {
        return new ChallengeId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    public Long toLong() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Challenge ID is not a valid number: " + value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChallengeId that = (ChallengeId) o;
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
