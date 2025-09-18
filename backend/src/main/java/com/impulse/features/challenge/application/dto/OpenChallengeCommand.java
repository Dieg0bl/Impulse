package com.impulse.features.challenge.application.dto;

import com.impulse.shared.enums.Visibility;

/**
 * Command DTO: OpenChallengeCommand
 * Represents a command to open a challenge
 */
public class OpenChallengeCommand {
    private final String challengeId;
    private final Long requestingUserId;
    private final Visibility visibility;
    private final String consentVersion;

    public OpenChallengeCommand(String challengeId, Long requestingUserId,
                               Visibility visibility, String consentVersion) {
        this.challengeId = challengeId;
        this.requestingUserId = requestingUserId;
        this.visibility = visibility;
        this.consentVersion = consentVersion;
    }

    // Getters
    public String getChallengeId() { return challengeId; }
    public Long getRequestingUserId() { return requestingUserId; }
    public Visibility getVisibility() { return visibility; }
    public String getConsentVersion() { return consentVersion; }
}
