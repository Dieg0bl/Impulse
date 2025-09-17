package com.impulse.application.coach.usecase;

import com.impulse.application.coach.dto.CoachResponse;
import com.impulse.application.coach.mapper.CoachAppMapper;
import com.impulse.application.coach.port.CoachRepository;
import com.impulse.domain.coach.Coach;
import com.impulse.domain.coach.CoachId;
import com.impulse.domain.coach.CoachDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetCoachByIdUseCase {
    private final CoachRepository coachRepository;
    private final CoachAppMapper coachMapper;
    public GetCoachByIdUseCase(CoachRepository coachRepository, CoachAppMapper coachMapper) {
        this.coachRepository = coachRepository;
        this.coachMapper = coachMapper;
    }
    public CoachResponse execute(Long coachId) {
        if (coachId == null) throw new CoachDomainError("Coach ID cannot be null");
        CoachId id = CoachId.of(coachId);
        Coach coach = coachRepository.findById(id)
            .orElseThrow(() -> new CoachDomainError("Coach not found with ID: " + coachId));
        return coachMapper.toResponse(coach);
    }
}
