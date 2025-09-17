package com.impulse.adapters.persistence.coach.mapper;

import com.impulse.adapters.persistence.coach.entity.CoachJpaEntity;
import com.impulse.domain.coach.Coach;
import com.impulse.domain.coach.CoachId;
import com.impulse.domain.user.UserId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CoachJpaMapper {

    public CoachJpaEntity toEntity(Coach coach) {
        if (coach == null) {
            return null;
        }

        CoachJpaEntity entity = new CoachJpaEntity();
        entity.setId(coach.getId() != null ? UUID.fromString(coach.getId().getValue()) : null);
        entity.setUserId(UUID.fromString(coach.getUserId().getValue()));
        entity.setBio(coach.getBio());
        entity.setSpecializations(coach.getSpecializations());
        entity.setYearsExperience(coach.getYearsExperience());
        entity.setHourlyRate(coach.getHourlyRate());
        entity.setCertificationUrl(coach.getCertificationUrl());
        entity.setStatus(coach.getStatus());
        entity.setIsVerified(coach.isVerified());
        entity.setAverageRating(coach.getAverageRating());
        entity.setTotalRatings(coach.getTotalRatings());
        entity.setTotalSessions(coach.getTotalSessions());
        entity.setCreatedAt(coach.getCreatedAt());
        entity.setUpdatedAt(coach.getUpdatedAt());

        return entity;
    }

    public Coach toDomain(CoachJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Coach.reconstruct(
                entity.getId() != null ? new CoachId(entity.getId().toString()) : null,
                new UserId(entity.getUserId().toString()),
                entity.getBio(),
                entity.getSpecializations(),
                entity.getYearsExperience(),
                entity.getHourlyRate(),
                entity.getCertificationUrl(),
                entity.getStatus(),
                entity.getIsVerified(),
                entity.getAverageRating(),
                entity.getTotalRatings(),
                entity.getTotalSessions(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}


