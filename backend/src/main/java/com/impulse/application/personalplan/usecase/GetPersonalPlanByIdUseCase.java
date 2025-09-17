package com.impulse.application.personalplan.usecase;

import com.impulse.application.personalplan.dto.PersonalPlanResponse;
import com.impulse.application.personalplan.mapper.PersonalPlanAppMapper;
import com.impulse.application.personalplan.port.PersonalPlanRepository;
import com.impulse.domain.personalplan.PersonalPlan;
import com.impulse.domain.personalplan.PersonalPlanId;
import com.impulse.domain.personalplan.PersonalPlanDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetPersonalPlanByIdUseCase {
    private final PersonalPlanRepository personalPlanRepository;
    private final PersonalPlanAppMapper personalPlanMapper;
    public GetPersonalPlanByIdUseCase(PersonalPlanRepository personalPlanRepository, PersonalPlanAppMapper personalPlanMapper) {
        this.personalPlanRepository = personalPlanRepository;
        this.personalPlanMapper = personalPlanMapper;
    }
    public PersonalPlanResponse execute(Long personalPlanId) {
        if (personalPlanId == null) throw new PersonalPlanDomainError("PersonalPlan ID cannot be null");
        PersonalPlanId id = PersonalPlanId.of(personalPlanId);
        PersonalPlan personalPlan = personalPlanRepository.findById(id)
            .orElseThrow(() -> new PersonalPlanDomainError("PersonalPlan not found with ID: " + personalPlanId));
        return personalPlanMapper.toResponse(personalPlan);
    }
}
