package com.impulse.shared.enums;

/**
 * Enum: EvidenceType
 * Represents different types of evidence that can be submitted
 */
public enum EvidenceType {
    TEXT("text"),
    IMAGE("image"),
    VIDEO("video"),
    FILE("file");

    private final String dbValue;

    EvidenceType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static EvidenceType fromDbValue(String dbValue) {
        for (EvidenceType type : values()) {
            if (type.dbValue.equals(dbValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown evidence type: " + dbValue);
    }
}
