package com.impulse.application.coachprofile.usecase;

import com.impulse.application.coachprofile.dto.CreateCoachProfileCommand;
import com.impulse.application.coachprofile.dto.CoachProfileResponse;
import com.impulse.application.coachprofile.mapper.CoachProfileAppMapper;
import com.impulse.application.coachprofile.port.CoachProfileRepository;
import com.impulse.domain.coachprofile.CoachProfile;
import com.impulse.domain.coachprofile.CoachProfileDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateCoachProfileUseCase {
    private final CoachProfileRepository coachProfileRepository;
    private final CoachProfileAppMapper coachProfileMapper;
    public CreateCoachProfileUseCase(CoachProfileRepository coachProfileRepository, CoachProfileAppMapper coachProfileMapper) {
        this.coachProfileRepository = coachProfileRepository;
        this.coachProfileMapper = coachProfileMapper;
    }
    public CoachProfileResponse execute(CreateCoachProfileCommand command) {
        validateCommand(command);
        CoachProfile coachProfile = coachProfileMapper.toDomain(command);
        CoachProfile saved = coachProfileRepository.save(coachProfile);
        return coachProfileMapper.toResponse(saved);
    }
    private void validateCommand(CreateCoachProfileCommand command) {
        if (command == null) throw new CoachProfileDomainError("CreateCoachProfileCommand cannot be null");
        if (command.getName() == null || command.getName().trim().isEmpty()) throw new CoachProfileDomainError("Name is required");
    }
}
