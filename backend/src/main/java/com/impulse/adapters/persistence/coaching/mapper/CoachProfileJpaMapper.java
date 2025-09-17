package com.impulse.adapters.persistence.coaching.mapper;

import com.impulse.adapters.persistence.coaching.entity.CoachProfileJpaEntity;
import com.impulse.domain.coachprofile.CoachProfile;
import com.impulse.domain.coachprofile.CoachProfileId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CoachProfileJpaMapper {

    public CoachProfileJpaEntity toEntity(CoachProfile domain) {
        if (domain == null) {
            return null;
        }

        CoachProfileJpaEntity entity = new CoachProfileJpaEntity();
        entity.setId(UUID.fromString(domain.getId().getValue()));
        entity.setUserId(domain.getUserId());
        entity.setSpecializations(domain.getSpecializations());
        entity.setBio(domain.getBio());
        entity.setExperience(domain.getExperience());
        entity.setCertifications(domain.getCertifications());
        entity.setAvgRating(domain.getAvgRating());
        entity.setTotalSessions(domain.getTotalSessions());
        entity.setHourlyRate(domain.getHourlyRate());
        entity.setAvailability(domain.getAvailability());
        entity.setIsActive(domain.getIsActive());

        return entity;
    }

    public CoachProfile toDomain(CoachProfileJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return CoachProfile.builder()
                .id(new CoachProfileId(entity.getId().toString()))
                .userId(entity.getUserId())
                .specializations(entity.getSpecializations())
                .bio(entity.getBio())
                .experience(entity.getExperience())
                .certifications(entity.getCertifications())
                .avgRating(entity.getAvgRating())
                .totalSessions(entity.getTotalSessions())
                .hourlyRate(entity.getHourlyRate())
                .availability(entity.getAvailability())
                .isActive(entity.getIsActive())
                .build();
    }
}
