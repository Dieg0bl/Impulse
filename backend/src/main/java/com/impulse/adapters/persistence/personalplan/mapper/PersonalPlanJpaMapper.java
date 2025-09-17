package com.impulse.adapters.persistence.personalplan.mapper;

import com.impulse.adapters.persistence.personalplan.entity.PersonalPlanJpaEntity;
import com.impulse.domain.personalplan.PersonalPlan;
import com.impulse.domain.personalplan.PersonalPlanId;
import org.springframework.stereotype.Component;

@Component
public class PersonalPlanJpaMapper {
    public PersonalPlanJpaEntity toEntity(PersonalPlan personalPlan) {
        PersonalPlanJpaEntity entity = new PersonalPlanJpaEntity();
        if (personalPlan.getId() != null) entity.setId(personalPlan.getId().getValue());
        entity.setUserId(personalPlan.getUserId());
        entity.setCoachId(personalPlan.getCoachId());
        entity.setPlanDetails(personalPlan.getPlanDetails());
        entity.setStartDate(personalPlan.getStartDate());
        entity.setEndDate(personalPlan.getEndDate());
        entity.setIsActive(personalPlan.getIsActive());
        return entity;
    }
    public PersonalPlan toDomain(PersonalPlanJpaEntity entity) {
        return PersonalPlan.builder()
                .id(entity.getId() != null ? PersonalPlanId.of(entity.getId()) : null)
                .userId(entity.getUserId())
                .coachId(entity.getCoachId())
                .planDetails(entity.getPlanDetails())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .isActive(entity.getIsActive())
                .build();
    }
}


