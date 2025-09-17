package com.impulse.domain.validation;

import java.util.Objects;

public class ValidationId {
    private final Long value;
    private ValidationId(Long value) { this.value = value; }
    public static ValidationId of(Long value) { return new ValidationId(value); }
    public Long getValue() { return value; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationId that = (ValidationId) o;
        return Objects.equals(value, that.value);
    }
    @Override
    public int hashCode() { return Objects.hash(value); }
    @Override
    public String toString() { return String.valueOf(value); }
}
