package com.impulse.features.challenge.adapters.in.rest.dto;

import com.impulse.shared.enums.ChallengeStatus;
import com.impulse.shared.enums.Visibility;
import java.time.LocalDateTime;

/**
 * API DTO: ChallengeApiResponse
 * HTTP response body for challenge operations
 * Contains only publicly accessible challenge data
 */
public class ChallengeApiResponse {

    private String id;
    private String title;
    private String description;
    private ChallengeStatus status;
    private Visibility visibility;
    private String category;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ChallengeApiResponse() {}

    public ChallengeApiResponse(String id, String title, String description,
                               ChallengeStatus status, Visibility visibility, String category,
                               LocalDateTime openedAt, LocalDateTime closedAt,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.visibility = visibility;
        this.category = category;
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ChallengeStatus getStatus() { return status; }
    public void setStatus(ChallengeStatus status) { this.status = status; }

    public Visibility getVisibility() { return visibility; }
    public void setVisibility(Visibility visibility) { this.visibility = visibility; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getOpenedAt() { return openedAt; }
    public void setOpenedAt(LocalDateTime openedAt) { this.openedAt = openedAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
