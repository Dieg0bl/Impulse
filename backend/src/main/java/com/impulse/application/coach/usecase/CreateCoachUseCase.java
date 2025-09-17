package com.impulse.application.coach.usecase;

import com.impulse.application.coach.dto.CreateCoachCommand;
import com.impulse.application.coach.dto.CoachResponse;
import com.impulse.application.coach.mapper.CoachAppMapper;
import com.impulse.application.coach.port.CoachRepository;
import com.impulse.domain.coach.Coach;
import com.impulse.domain.coach.CoachDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateCoachUseCase {
    private final CoachRepository coachRepository;
    private final CoachAppMapper coachMapper;
    public CreateCoachUseCase(CoachRepository coachRepository, CoachAppMapper coachMapper) {
        this.coachRepository = coachRepository;
        this.coachMapper = coachMapper;
    }
    public CoachResponse execute(CreateCoachCommand command) {
        validateCommand(command);
        Coach coach = coachMapper.toDomain(command);
        Coach savedCoach = coachRepository.save(coach);
        return coachMapper.toResponse(savedCoach);
    }
    private void validateCommand(CreateCoachCommand command) {
        if (command == null) throw new CoachDomainError("CreateCoachCommand cannot be null");
        if (command.getUserId() == null || command.getUserId().trim().isEmpty()) throw new CoachDomainError("UserId is required");
        if (command.getYearsExperience() != null && command.getYearsExperience() < 0) throw new CoachDomainError("Years of experience cannot be negative");
        if (command.getHourlyRate() != null && command.getHourlyRate().compareTo(java.math.BigDecimal.ZERO) < 0) throw new CoachDomainError("Hourly rate cannot be negative");
    }
}
