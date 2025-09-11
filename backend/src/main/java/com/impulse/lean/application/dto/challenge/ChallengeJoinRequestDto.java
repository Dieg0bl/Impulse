package com.impulse.lean.application.dto.challenge;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

/**
 * IMPULSE LEAN v1 - Challenge Join Request DTO
 * 
 * DTO for joining challenges
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public class ChallengeJoinRequestDto {

    @Size(max = 500, message = "Join message too long")
    private String message; // Optional message when joining

    private boolean acceptTerms = false;

    // Default constructor required for JSON deserialization
    public ChallengeJoinRequestDto() {
        // Empty constructor for framework compatibility
    }

    public ChallengeJoinRequestDto(String message, boolean acceptTerms) {
        this.message = message;
        this.acceptTerms = acceptTerms;
    }

    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isAcceptTerms() { return acceptTerms; }
    public void setAcceptTerms(boolean acceptTerms) { this.acceptTerms = acceptTerms; }

    @AssertTrue(message = "Must accept terms to join challenge")
    public boolean isTermsAccepted() {
        return acceptTerms;
    }

    @Override
    public String toString() {
        return "ChallengeJoinRequestDto{" +
                "message='" + message + '\'' +
                ", acceptTerms=" + acceptTerms +
                '}';
    }
}
