package com.impulse.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for challenge creation requests
 */
@Data
public class ChallengeCreateDto {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    @NotBlank(message = "Challenge type is required")
    @Size(max = 50, message = "Challenge type must not exceed 50 characters")
    private String challengeType;
    
    private Double rewardAmount;
    
    private Integer maxParticipants;
}
