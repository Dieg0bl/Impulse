package com.impulse.shared.enums;

/** Consent types (stored lowercase in DB) */
public enum ConsentKey {
	TOS("tos"), PRIVACY("privacy"), MARKETING("marketing");

	private final String dbValue;
	ConsentKey(String dbValue) { this.dbValue = dbValue; }
	public String getDbValue() { return dbValue; }
}
