package com.impulse.application.personalplan.usecase;

import com.impulse.application.personalplan.dto.CreatePersonalPlanCommand;
import com.impulse.application.personalplan.dto.PersonalPlanResponse;
import com.impulse.application.personalplan.mapper.PersonalPlanAppMapper;
import com.impulse.application.personalplan.port.PersonalPlanRepository;
import com.impulse.domain.personalplan.PersonalPlan;
import com.impulse.domain.personalplan.PersonalPlanDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreatePersonalPlanUseCase {
    private final PersonalPlanRepository personalPlanRepository;
    private final PersonalPlanAppMapper personalPlanMapper;
    public CreatePersonalPlanUseCase(PersonalPlanRepository personalPlanRepository, PersonalPlanAppMapper personalPlanMapper) {
        this.personalPlanRepository = personalPlanRepository;
        this.personalPlanMapper = personalPlanMapper;
    }
    public PersonalPlanResponse execute(CreatePersonalPlanCommand command) {
        validateCommand(command);
        PersonalPlan personalPlan = personalPlanMapper.toDomain(command);
        PersonalPlan saved = personalPlanRepository.save(personalPlan);
        return personalPlanMapper.toResponse(saved);
    }
    private void validateCommand(CreatePersonalPlanCommand command) {
        if (command == null) throw new PersonalPlanDomainError("CreatePersonalPlanCommand cannot be null");
        if (command.getUserId() == null || command.getUserId().trim().isEmpty()) throw new PersonalPlanDomainError("UserId is required");
        if (command.getCoachId() == null || command.getCoachId().trim().isEmpty()) throw new PersonalPlanDomainError("CoachId is required");
        if (command.getPlanDetails() == null || command.getPlanDetails().trim().isEmpty()) throw new PersonalPlanDomainError("Plan details are required");
    }
}
