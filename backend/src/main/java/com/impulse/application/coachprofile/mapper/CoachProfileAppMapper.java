package com.impulse.application.coachprofile.mapper;

import com.impulse.application.coachprofile.dto.CreateCoachProfileCommand;
import com.impulse.application.coachprofile.dto.CoachProfileResponse;
import com.impulse.domain.coachprofile.CoachProfile;
import com.impulse.domain.coachprofile.CoachProfileId;
import org.springframework.stereotype.Component;

@Component
public class CoachProfileAppMapper {
    public CoachProfile toDomain(CreateCoachProfileCommand command) {
        return CoachProfile.builder()
                .id(null)
                .name(command.getName())
                .photo(command.getPhoto())
                .specializations(command.getSpecializations())
                .languages(command.getLanguages())
                .timezone(command.getTimezone())
                .yearsExperience(command.getYearsExperience())
                .certificationsCount(command.getCertificationsCount())
                .totalStudents(command.getTotalStudents())
                .averageRating(command.getAverageRating())
                .successRate(command.getSuccessRate())
                .isActive(command.getIsActive())
                .build();
    }
    public CoachProfileResponse toResponse(CoachProfile coachProfile) {
        return CoachProfileResponse.builder()
                .id(coachProfile.getId() != null ? coachProfile.getId().getValue() : null)
                .name(coachProfile.getName())
                .photo(coachProfile.getPhoto())
                .specializations(coachProfile.getSpecializations())
                .languages(coachProfile.getLanguages())
                .timezone(coachProfile.getTimezone())
                .yearsExperience(coachProfile.getYearsExperience())
                .certificationsCount(coachProfile.getCertificationsCount())
                .totalStudents(coachProfile.getTotalStudents())
                .averageRating(coachProfile.getAverageRating())
                .successRate(coachProfile.getSuccessRate())
                .isActive(coachProfile.getIsActive())
                .build();
    }
}
