package com.impulse.lean.domain.model;

/**
 * IMPULSE LEAN v1 - Validation Decision Enum
 * 
 * Represents the final decision made during evidence validation
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public enum ValidationDecision {
    
    /**
     * Evidence is approved and meets all requirements
     */
    APPROVED("Approved", "Evidence is valid and meets all requirements"),
    
    /**
     * Evidence is approved with minor conditions or recommendations
     */
    APPROVED_WITH_CONDITIONS("Approved with Conditions", "Evidence is approved but with minor recommendations"),
    
    /**
     * Evidence is rejected due to major issues or non-compliance
     */
    REJECTED("Rejected", "Evidence does not meet requirements and is rejected"),
    
    /**
     * Evidence requires modifications before resubmission
     */
    NEEDS_REVISION("Needs Revision", "Evidence requires modifications and resubmission"),
    
    /**
     * Evidence is incomplete and needs additional information
     */
    INCOMPLETE("Incomplete", "Evidence is missing required information"),
    
    /**
     * Evidence is referred to specialist for expert review
     */
    REFERRED_TO_SPECIALIST("Referred to Specialist", "Evidence requires specialized expert review"),
    
    /**
     * Evidence requires additional verification or documentation
     */
    REQUIRES_VERIFICATION("Requires Verification", "Evidence needs additional verification"),
    
    /**
     * Evidence is outside the scope of the current validation
     */
    OUT_OF_SCOPE("Out of Scope", "Evidence is outside the validation scope");

    private final String displayName;
    private final String description;

    ValidationDecision(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if this decision represents approval
     */
    public boolean isApproved() {
        return this == APPROVED || this == APPROVED_WITH_CONDITIONS;
    }

    /**
     * Check if this decision represents rejection
     */
    public boolean isRejected() {
        return this == REJECTED;
    }

    /**
     * Check if this decision requires action from submitter
     */
    public boolean requiresAction() {
        return this == NEEDS_REVISION || this == INCOMPLETE || this == REQUIRES_VERIFICATION;
    }

    /**
     * Check if this decision requires escalation
     */
    public boolean requiresEscalation() {
        return this == REFERRED_TO_SPECIALIST || this == OUT_OF_SCOPE;
    }

    /**
     * Check if evidence can be resubmitted after this decision
     */
    public boolean allowsResubmission() {
        return this == NEEDS_REVISION || this == INCOMPLETE || this == REQUIRES_VERIFICATION;
    }

    /**
     * Get the severity level of this decision for reporting
     */
    public DecisionSeverity getSeverity() {
        switch (this) {
            case APPROVED:
                return DecisionSeverity.LOW;
            case APPROVED_WITH_CONDITIONS:
                return DecisionSeverity.LOW;
            case NEEDS_REVISION:
            case INCOMPLETE:
            case REQUIRES_VERIFICATION:
                return DecisionSeverity.MEDIUM;
            case REJECTED:
            case OUT_OF_SCOPE:
                return DecisionSeverity.HIGH;
            case REFERRED_TO_SPECIALIST:
                return DecisionSeverity.CRITICAL;
            default:
                return DecisionSeverity.MEDIUM;
        }
    }

    /**
     * Severity levels for validation decisions
     */
    public enum DecisionSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
