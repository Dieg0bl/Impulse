package com.impulse.application.coach.mapper;

import com.impulse.application.coach.dto.CreateCoachCommand;
import com.impulse.application.coach.dto.CoachResponse;
import com.impulse.domain.coach.Coach;
import com.impulse.domain.coach.CoachId;
import com.impulse.domain.user.UserId;
import org.springframework.stereotype.Component;

/**
 * CoachAppMapper - Mapper entre DTOs de aplicación y entidades de dominio
 */
@Component
public class CoachAppMapper {
    public Coach toDomain(CreateCoachCommand command) {
        if (command == null) return null;
        return Coach.createNew(
            UserId.of(Long.parseLong(command.getUserId())),
            command.getBio(),
            command.getSpecializations(),
            command.getYearsExperience(),
            command.getHourlyRate(),
            command.getCertificationUrl()
        );
    }
    public CoachResponse toResponse(Coach coach) {
        if (coach == null) return null;
        return new CoachResponse(
            coach.getId() != null ? coach.getId().asLong() : null,
            coach.getUserId() != null ? coach.getUserId().asLong().toString() : null,
            coach.getBio(),
            coach.getSpecializations(),
            coach.getYearsExperience(),
            coach.getHourlyRate(),
            coach.getStatus(),
            coach.getCertificationUrl(),
            coach.getAverageRating(),
            coach.getTotalRatings(),
            coach.getTotalSessions(),
            coach.getIsVerified(),
            coach.getCreatedAt(),
            coach.getUpdatedAt()
        );
    }
}
