package com.impulse.domain.validatorassignment;

import java.util.Objects;

public class ValidatorAssignmentId {
    private final String value;
    private ValidatorAssignmentId(String value) { this.value = value; }
    public static ValidatorAssignmentId of(String value) { return new ValidatorAssignmentId(value); }
    public String getValue() { return value; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidatorAssignmentId that = (ValidatorAssignmentId) o;
        return Objects.equals(value, that.value);
    }
    @Override
    public int hashCode() { return Objects.hash(value); }
    @Override
    public String toString() { return String.valueOf(value); }
}
