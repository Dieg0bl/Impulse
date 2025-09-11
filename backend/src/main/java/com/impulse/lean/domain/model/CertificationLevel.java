package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Certification Level Enum
 * 
 * Certification levels for validators
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum CertificationLevel {
    BASIC("Basic", "Basic validation certification"),
    INTERMEDIATE("Intermediate", "Intermediate validation certification"),
    ADVANCED("Advanced", "Advanced validation certification"),
    EXPERT("Expert", "Expert level validation certification"),
    MASTER("Master", "Master level validation certification");

    private final String displayName;
    private final String description;

    CertificationLevel(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isBasic() {
        return this == BASIC;
    }

    public boolean isAdvanced() {
        return this == ADVANCED || this == EXPERT || this == MASTER;
    }

    public boolean canMentor() {
        return this == EXPERT || this == MASTER;
    }

    public int getLevel() {
        return ordinal() + 1;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
