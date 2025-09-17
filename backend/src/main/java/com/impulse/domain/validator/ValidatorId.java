package com.impulse.domain.validator;

import java.util.Objects;

public class ValidatorId {
    private final String value;
    private ValidatorId(String value) { this.value = value; }
    public static ValidatorId of(String value) { return new ValidatorId(value); }
    public String getValue() { return value; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidatorId that = (ValidatorId) o;
        return Objects.equals(value, that.value);
    }
    @Override
    public int hashCode() { return Objects.hash(value); }
    @Override
    public String toString() { return String.valueOf(value); }
}
