package com.impulse.shared.enums;

public enum RefundStatus {
	PENDING("pending"), SUCCEEDED("succeeded"), FAILED("failed"), CANCELED("canceled");
	private final String db; RefundStatus(String db){this.db=db;} public String getDb(){return db;}
}
