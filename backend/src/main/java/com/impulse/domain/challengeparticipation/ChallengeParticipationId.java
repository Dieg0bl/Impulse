package com.impulse.domain.challengeparticipation;

import java.util.Objects;

public class ChallengeParticipationId {
    private final Long value;
    private ChallengeParticipationId(Long value) { this.value = value; }
    public static ChallengeParticipationId of(Long value) { return new ChallengeParticipationId(value); }
    public Long getValue() { return value; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChallengeParticipationId that = (ChallengeParticipationId) o;
        return Objects.equals(value, that.value);
    }
    @Override
    public int hashCode() { return Objects.hash(value); }
    @Override
    public String toString() { return String.valueOf(value); }
}
