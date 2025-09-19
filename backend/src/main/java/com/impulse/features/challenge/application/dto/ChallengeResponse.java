package com.impulse.features.challenge.application.dto;

import java.time.LocalDateTime;

import com.impulse.shared.enums.ChallengeStatus;
import com.impulse.shared.enums.Visibility;

/**
 * Response DTO: ChallengeResponse
 * Represents challenge data returned from use cases
 */
public class ChallengeResponse {
    private final String id;
    private final Long ownerUserId;
    private final String title;
    private final String description;
    private final ChallengeStatus status;
    private final Visibility visibility;
    private final String category;
    private final String publicConsentVersion;
    private final LocalDateTime openedAt;
    private final LocalDateTime closedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final boolean isDemo;
    private final boolean isTemplate;

    public ChallengeResponse(String id, Long ownerUserId, String title, String description,
                           ChallengeStatus status, Visibility visibility, String category,
                           String publicConsentVersion, LocalDateTime openedAt, LocalDateTime closedAt,
                           LocalDateTime createdAt, LocalDateTime updatedAt,
                           boolean isDemo, boolean isTemplate) {
        this.id = id;
        this.ownerUserId = ownerUserId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.visibility = visibility;
        this.category = category;
        this.publicConsentVersion = publicConsentVersion;
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDemo = isDemo;
        this.isTemplate = isTemplate;
    }

    // Getters
    public String getId() { return id; }
    public Long getOwnerUserId() { return ownerUserId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ChallengeStatus getStatus() { return status; }
    public Visibility getVisibility() { return visibility; }
    public String getCategory() { return category; }
    public String getPublicConsentVersion() { return publicConsentVersion; }
    public LocalDateTime getOpenedAt() { return openedAt; }
    public LocalDateTime getClosedAt() { return closedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public boolean isDemo() { return isDemo; }
    public boolean isTemplate() { return isTemplate; }
}
