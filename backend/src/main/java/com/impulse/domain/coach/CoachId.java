package com.impulse.domain.coach;

import java.util.Objects;

/**
 * CoachId - Value Object para identificadores de coach
 */
public class CoachId {
    private final Long value;

    private CoachId(Long value) {
        if (value == null) throw new IllegalArgumentException("CoachId value cannot be null");
        this.value = value;
    }

    public static CoachId of(Long value) {
        return new CoachId(value);
    }

    public Long asLong() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CoachId)) return false;
        CoachId coachId = (CoachId) o;
        return Objects.equals(value, coachId.value);
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
