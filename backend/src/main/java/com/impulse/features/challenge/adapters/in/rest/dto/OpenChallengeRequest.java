package com.impulse.features.challenge.adapters.in.rest.dto;

import com.impulse.shared.enums.Visibility;
import jakarta.validation.constraints.NotBlank;

/**
 * API DTO: OpenChallengeRequest
 * HTTP request body for opening challenges
 */
public class OpenChallengeRequest {

    private Visibility visibility;

    @NotBlank(message = "Consent version is required")
    private String consentVersion;

    // Constructors
    public OpenChallengeRequest() {}

    public OpenChallengeRequest(Visibility visibility, String consentVersion) {
        this.visibility = visibility;
        this.consentVersion = consentVersion;
    }

    // Getters and Setters
    public Visibility getVisibility() { return visibility; }
    public void setVisibility(Visibility visibility) { this.visibility = visibility; }

    public String getConsentVersion() { return consentVersion; }
    public void setConsentVersion(String consentVersion) { this.consentVersion = consentVersion; }
}
