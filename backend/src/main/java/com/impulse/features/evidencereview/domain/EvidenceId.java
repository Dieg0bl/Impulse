package com.impulse.features.evidencereview.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object: EvidenceId
 * Represents a unique identifier for Evidence
 */
public class EvidenceId {
    private final String value;

    private EvidenceId(String value) {
        this.value = Objects.requireNonNull(value, "Evidence ID value cannot be null");
    }

    public static EvidenceId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Evidence ID cannot be null or empty");
        }
        return new EvidenceId(value.trim());
    }

    public static EvidenceId of(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Evidence ID must be a positive number");
        }
        return new EvidenceId(id.toString());
    }

    public static EvidenceId generate() {
        return new EvidenceId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    public Long toLong() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Evidence ID is not a valid number: " + value);
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
        return value;
    }
}
