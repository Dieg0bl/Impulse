package com.impulse.infrastructure.web.dto;

import com.impulse.domain.challenge.ChallengeStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for challenge response data
 */
@Data
public class ChallengeResponseDto {
    
    private String id;
    private String title;
    private String description;
    private String challengeType;
    private ChallengeStatus status;
    private Double rewardAmount;
    private Integer maxParticipants;
    private String creatorId;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
