package com.impulse.adapters.persistence.rating.mapper;

import com.impulse.adapters.persistence.rating.entity.RatingJpaEntity;
import com.impulse.domain.rating.Rating;
import com.impulse.domain.rating.RatingId;
import com.impulse.domain.enums.RatingType;
import org.springframework.stereotype.Component;

@Component
public class RatingJpaMapper {
    public RatingJpaEntity toEntity(Rating rating) {
        RatingJpaEntity entity = new RatingJpaEntity();
        if (rating.getId() != null) entity.setId(rating.getId().getValue());
        entity.setScore(rating.getScore());
        entity.setComment(rating.getComment());
        entity.setType(rating.getType());
        entity.setIsAnonymous(rating.getIsAnonymous());
        entity.setCreatedAt(rating.getCreatedAt());
        entity.setUpdatedAt(rating.getUpdatedAt());
        entity.setRaterUserId(rating.getRaterUserId());
        entity.setChallengeId(rating.getChallengeId());
        entity.setEvidenceId(rating.getEvidenceId());
        entity.setCoachId(rating.getCoachId());
        return entity;
    }
    public Rating toDomain(RatingJpaEntity entity) {
        return Rating.builder()
                .id(entity.getId() != null ? RatingId.of(entity.getId()) : null)
                .score(entity.getScore())
                .comment(entity.getComment())
                .type(entity.getType() != null ? entity.getType() : RatingType.GENERAL)
                .isAnonymous(entity.getIsAnonymous())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .raterUserId(entity.getRaterUserId())
                .challengeId(entity.getChallengeId())
                .evidenceId(entity.getEvidenceId())
                .coachId(entity.getCoachId())
                .build();
    }
}


