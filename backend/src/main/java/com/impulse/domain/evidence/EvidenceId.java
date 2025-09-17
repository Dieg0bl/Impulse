package com.impulse.domain.evidence;

import java.util.Objects;
import java.util.UUID;

/**
 * EvidenceId - Value Object para identificadores de evidencia
 */
public class EvidenceId {

    private final String value;

    private EvidenceId(String value) {
        this.value = Objects.requireNonNull(value, "EvidenceId value cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("EvidenceId value cannot be empty");
        }
    }

    public static EvidenceId of(String value) {
        return new EvidenceId(value);
    }

    public static EvidenceId of(Long value) {
        return new EvidenceId(value.toString());
    }

    public static EvidenceId generate() {
        return new EvidenceId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    public Long asLong() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("EvidenceId is not a valid Long: " + value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EvidenceId that = (EvidenceId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "EvidenceId{" + value + '}';
    }
}
