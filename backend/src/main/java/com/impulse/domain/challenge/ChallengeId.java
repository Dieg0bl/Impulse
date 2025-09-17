package com.impulse.domain.challenge;

import java.util.Objects;
import java.util.UUID;

/**
 * ChallengeId - Value Object para identificadores de challenge
 */
public class ChallengeId {

    private final String value;

    private ChallengeId(String value) {
        this.value = Objects.requireNonNull(value, "ChallengeId value cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("ChallengeId value cannot be empty");
        }
    }

    public static ChallengeId of(String value) {
        return new ChallengeId(value);
    }

    public static ChallengeId of(Long value) {
        return new ChallengeId(value.toString());
    }

    public static ChallengeId generate() {
        return new ChallengeId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    public Long asLong() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("ChallengeId is not a valid Long: " + value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChallengeId challengeId = (ChallengeId) o;
        return Objects.equals(value, challengeId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ChallengeId{" + value + '}';
    }
}
