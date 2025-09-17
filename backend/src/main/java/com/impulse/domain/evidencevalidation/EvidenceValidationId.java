package com.impulse.domain.evidencevalidation;

import java.util.Objects;

public class EvidenceValidationId {
    private final String value;
    private EvidenceValidationId(String value) { this.value = value; }
    public static EvidenceValidationId of(String value) { return new EvidenceValidationId(value); }
    public String getValue() { return value; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EvidenceValidationId that = (EvidenceValidationId) o;
        return Objects.equals(value, that.value);
    }
    @Override
    public int hashCode() { return Objects.hash(value); }
    @Override
    public String toString() { return String.valueOf(value); }
}
