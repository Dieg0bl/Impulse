package com.impulse.domain.coachingsession;

import java.util.Objects;

public class CoachingSessionId {
    private final String value;
    private CoachingSessionId(String value) { this.value = value; }
    public static CoachingSessionId of(String value) { return new CoachingSessionId(value); }
    public String getValue() { return value; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoachingSessionId that = (CoachingSessionId) o;
        return Objects.equals(value, that.value);
    }
    @Override
    public int hashCode() { return Objects.hash(value); }
    @Override
    public String toString() { return String.valueOf(value); }
}
