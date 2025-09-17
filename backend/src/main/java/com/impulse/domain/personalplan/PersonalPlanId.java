package com.impulse.domain.personalplan;

import java.util.Objects;

public class PersonalPlanId {
    private final Long value;
    private PersonalPlanId(Long value) { this.value = value; }
    public static PersonalPlanId of(Long value) { return new PersonalPlanId(value); }
    public Long getValue() { return value; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonalPlanId that = (PersonalPlanId) o;
        return Objects.equals(value, that.value);
    }
    @Override
    public int hashCode() { return Objects.hash(value); }
    @Override
    public String toString() { return String.valueOf(value); }
}
