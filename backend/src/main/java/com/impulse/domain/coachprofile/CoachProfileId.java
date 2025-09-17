package com.impulse.domain.coachprofile;

import java.util.Objects;

public class CoachProfileId {
    private final String value;
    private CoachProfileId(String value) { this.value = value; }
    public static CoachProfileId of(String value) { return new CoachProfileId(value); }
    public String getValue() { return value; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoachProfileId that = (CoachProfileId) o;
        return Objects.equals(value, that.value);
    }
    @Override
    public int hashCode() { return Objects.hash(value); }
    @Override
    public String toString() { return String.valueOf(value); }
}
