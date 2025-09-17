package com.impulse.application.rating.mapper;

import com.impulse.application.rating.dto.CreateRatingCommand;
import com.impulse.application.rating.dto.RatingResponse;
import com.impulse.domain.rating.Rating;
import com.impulse.domain.rating.RatingId;
import org.springframework.stereotype.Component;

@Component
public class RatingAppMapper {
    public Rating toDomain(CreateRatingCommand command) {
        return Rating.builder()
                .id(null)
                .score(command.getScore())
                .comment(command.getComment())
                .type(command.getType())
                .isAnonymous(command.getIsAnonymous())
                .createdAt(command.getCreatedAt())
                .updatedAt(command.getUpdatedAt())
                .raterUserId(command.getRaterUserId())
                .challengeId(command.getChallengeId())
                .evidenceId(command.getEvidenceId())
                .coachId(command.getCoachId())
                .build();
    }
    public RatingResponse toResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId() != null ? rating.getId().getValue() : null)
                .score(rating.getScore())
                .comment(rating.getComment())
                .type(rating.getType())
                .isAnonymous(rating.getIsAnonymous())
                .createdAt(rating.getCreatedAt())
                .updatedAt(rating.getUpdatedAt())
                .raterUserId(rating.getRaterUserId())
                .challengeId(rating.getChallengeId())
                .evidenceId(rating.getEvidenceId())
                .coachId(rating.getCoachId())
                .build();
    }
}
