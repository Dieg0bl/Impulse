package com.impulse.lean.presentation.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

/**
 * IMPULSE LEAN v1 - Challenge Join Request DTO
 * 
 * Data Transfer Object for joining a challenge.
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public class ChallengeJoinRequestDto {

    @AssertTrue(message = "You must accept the challenge terms to participate")
    private Boolean acceptTerms;

    @Size(max = 500, message = "Join message cannot exceed 500 characters")
    private String joinMessage;

    // Getters and setters
    public Boolean getAcceptTerms() {
        return acceptTerms;
    }

    public void setAcceptTerms(Boolean acceptTerms) {
        this.acceptTerms = acceptTerms;
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public void setJoinMessage(String joinMessage) {
        this.joinMessage = joinMessage;
    }
}
