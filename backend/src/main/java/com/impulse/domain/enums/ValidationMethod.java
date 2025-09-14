package com.impulse.domain.enums;

/**
 * Enum representing different validation methods for challenges
 */
public enum ValidationMethod {
    /**
     * Manual validation by community validators
     */
    MANUAL("Manual validation by validators"),

    /**
     * Automatic validation using AI/ML algorithms
     */
    AUTOMATIC("Automatic validation using AI"),

    /**
     * Peer-to-peer validation by other participants
     */
    PEER_TO_PEER("Peer validation by participants"),

    /**
     * Self-validation by the participant
     */
    SELF_VALIDATION("Self-validation by participant"),

    /**
     * Validation through external API or service
     */
    EXTERNAL_API("Validation through external API"),

    /**
     * Validation through QR code scanning
     */
    QR_CODE("QR code validation"),

    /**
     * Validation through GPS/location verification
     */
    GPS_LOCATION("GPS location validation"),

    /**
     * Validation through time-based evidence
     */
    TIME_BASED("Time-based validation"),

    /**
     * Validation through image recognition
     */
    IMAGE_RECOGNITION("Image recognition validation"),

    /**
     * Hybrid validation combining multiple methods
     */
    HYBRID("Hybrid validation method");

    private final String description;

    ValidationMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if this validation method requires human intervention
     */
    public boolean requiresHumanValidation() {
        return this == MANUAL || this == PEER_TO_PEER;
    }

    /**
     * Check if this validation method is automated
     */
    public boolean isAutomated() {
        return this == AUTOMATIC || this == EXTERNAL_API ||
               this == QR_CODE || this == GPS_LOCATION ||
               this == TIME_BASED || this == IMAGE_RECOGNITION;
    }

    /**
     * Check if this validation method requires external services
     */
    public boolean requiresExternalService() {
        return this == EXTERNAL_API || this == GPS_LOCATION ||
               this == IMAGE_RECOGNITION;
    }

    /**
     * Get validation method from string value
     */
    public static ValidationMethod fromString(String value) {
        if (value == null) {
            return null;
        }

        try {
            return ValidationMethod.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return this.name();
    }
}
