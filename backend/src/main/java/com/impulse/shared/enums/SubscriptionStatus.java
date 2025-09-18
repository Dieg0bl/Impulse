package com.impulse.shared.enums;

/** Stripe subscription status subset used internally (db values lower-case) */
public enum SubscriptionStatus {
    ACTIVE("active"), CANCELED("canceled"), INCOMPLETE("incomplete"), INCOMPLETE_EXPIRED("incomplete_expired"),
    PAST_DUE("past_due"), TRIALING("trialing"), UNPAID("unpaid");

    private final String db;
    SubscriptionStatus(String db) { this.db = db; }
    public String getDb() { return db; }
}
