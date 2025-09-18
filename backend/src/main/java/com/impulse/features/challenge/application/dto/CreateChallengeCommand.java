package com.impulse.features.challenge.application.dto;

import java.time.LocalDateTime;

/**
 * Command DTO: CreateChallengeCommand
 * Represents a command to create a new challenge
 * Follows IMPULSE v1.0 specification
 */
public class CreateChallengeCommand {
    private final Long ownerUserId;
    private final String title;
    private final String description;
    private final String category;
    private final LocalDateTime timestamp;

    public CreateChallengeCommand(Long ownerUserId, String title, String description, String category) {
        this.ownerUserId = ownerUserId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public Long getOwnerUserId() { return ownerUserId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "CreateChallengeCommand{" +
                "ownerUserId=" + ownerUserId +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
