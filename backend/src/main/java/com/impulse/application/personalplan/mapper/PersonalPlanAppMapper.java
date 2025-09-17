package com.impulse.application.personalplan.mapper;

import com.impulse.application.personalplan.dto.CreatePersonalPlanCommand;
import com.impulse.application.personalplan.dto.PersonalPlanResponse;
import com.impulse.domain.personalplan.PersonalPlan;
import com.impulse.domain.personalplan.PersonalPlanId;
import org.springframework.stereotype.Component;

@Component
public class PersonalPlanAppMapper {
    public PersonalPlan toDomain(CreatePersonalPlanCommand command) {
        return PersonalPlan.builder()
                .id(null)
                .userId(command.getUserId())
                .coachId(command.getCoachId())
                .planDetails(command.getPlanDetails())
                .startDate(command.getStartDate())
                .endDate(command.getEndDate())
                .isActive(command.getIsActive())
                .build();
    }
    public PersonalPlanResponse toResponse(PersonalPlan personalPlan) {
        return PersonalPlanResponse.builder()
                .id(personalPlan.getId() != null ? personalPlan.getId().getValue() : null)
                .userId(personalPlan.getUserId())
                .coachId(personalPlan.getCoachId())
                .planDetails(personalPlan.getPlanDetails())
                .startDate(personalPlan.getStartDate())
                .endDate(personalPlan.getEndDate())
                .isActive(personalPlan.getIsActive())
                .build();
    }
}
