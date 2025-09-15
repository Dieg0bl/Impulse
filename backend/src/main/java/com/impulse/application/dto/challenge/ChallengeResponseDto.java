package com.impulse.application.dto.challenge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.impulse.domain.enums.ChallengeCategory;
import com.impulse.domain.enums.ChallengeStatus;
import com.impulse.domain.enums.ChallengeDifficulty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Challenge Response
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponseDto {
    private Long id;
    private String uuid;
    private String title;
    private String description;
    private ChallengeCategory category;
    private ChallengeDifficulty difficulty;
    private ChallengeStatus status;
    private BigDecimal rewardAmount;
    private String rewardCurrency;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long creatorId;
    private String creatorUsername;
    private Integer participantCount;
    private Integer maxParticipants;
    private Boolean isPublic;

}
