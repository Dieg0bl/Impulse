package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Confidence Level Enum
 * 
 * Represents the validator's confidence level in their validation decision
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum ConfidenceLevel {
    
    /**
     * Very low confidence - validator is uncertain about the decision
     */
    VERY_LOW("Very Low", "Validator has very low confidence in the decision", 1),
    
    /**
     * Low confidence - validator has some doubts about the decision
     */
    LOW("Low", "Validator has low confidence in the decision", 2),
    
    /**
     * Medium confidence - validator is moderately confident about the decision
     */
    MEDIUM("Medium", "Validator has medium confidence in the decision", 3),
    
    /**
     * High confidence - validator is quite confident about the decision
     */
    HIGH("High", "Validator has high confidence in the decision", 4),
    
    /**
     * Very high confidence - validator is extremely confident about the decision
     */
    VERY_HIGH("Very High", "Validator has very high confidence in the decision", 5);

    private final String displayName;
    private final String description;
    private final int numericValue;

    ConfidenceLevel(String displayName, String description, int numericValue) {
        this.displayName = displayName;
        this.description = description;
        this.numericValue = numericValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getNumericValue() {
        return numericValue;
    }

    /**
     * Check if this confidence level requires review
     */
    public boolean requiresReview() {
        return this == VERY_LOW || this == LOW;
    }

    /**
     * Check if this confidence level is acceptable for final decision
     */
    public boolean isAcceptableForFinalDecision() {
        return this == MEDIUM || this == HIGH || this == VERY_HIGH;
    }

    /**
     * Get confidence level from numeric value
     */
    public static ConfidenceLevel fromNumericValue(int value) {
        for (ConfidenceLevel level : values()) {
            if (level.numericValue == value) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid confidence level value: " + value);
    }

    /**
     * Get minimum confidence level for automatic processing
     */
    public static ConfidenceLevel getMinimumForAutomaticProcessing() {
        return MEDIUM;
    }
}
