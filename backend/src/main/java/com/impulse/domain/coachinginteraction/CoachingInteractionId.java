package com.impulse.domain.coachinginteraction;

import java.util.Objects;

public class CoachingInteractionId {
    private final String value;
    private CoachingInteractionId(String value) { this.value = value; }
    public static CoachingInteractionId of(String value) { return new CoachingInteractionId(value); }
    public String getValue() { return value; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoachingInteractionId that = (CoachingInteractionId) o;
        return Objects.equals(value, that.value);
    }
    @Override
    public int hashCode() { return Objects.hash(value); }
    @Override
    public String toString() { return String.valueOf(value); }
}
