package com.impulse.shared.enums;

public enum InvoiceStatus {
	DRAFT("draft"), OPEN("open"), PAID("paid"), VOID("void"), UNCOLLECTIBLE("uncollectible");
	private final String db; InvoiceStatus(String db){this.db=db;} public String getDb(){return db;}
}
