package com.impulse.features.challenge.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * API DTO: CreateChallengeRequest
 * HTTP request body for creating challenges
 */
public class CreateChallengeRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @Size(max = 65535, message = "Description is too long")
    private String description;

    @Size(max = 100, message = "Category cannot exceed 100 characters")
    private String category;

    // Constructors
    public CreateChallengeRequest() {}

    public CreateChallengeRequest(String title, String description, String category) {
        this.title = title;
        this.description = description;
        this.category = category;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
