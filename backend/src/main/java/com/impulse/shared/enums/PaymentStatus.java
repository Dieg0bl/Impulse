package com.impulse.shared.enums;

/** Stripe payment intent status (mapped) */
public enum PaymentStatus {
	SUCCEEDED("succeeded"), FAILED("failed"), REQUIRES_ACTION("requires_action"), PROCESSING("processing"), CANCELED("canceled");
	private final String db;
	PaymentStatus(String db) { this.db = db; }
	public String getDb() { return db; }
}
