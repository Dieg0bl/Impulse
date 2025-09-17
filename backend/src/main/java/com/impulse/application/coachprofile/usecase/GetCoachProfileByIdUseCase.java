package com.impulse.application.coachprofile.usecase;

import com.impulse.application.coachprofile.dto.CoachProfileResponse;
import com.impulse.application.coachprofile.mapper.CoachProfileAppMapper;
import com.impulse.application.coachprofile.port.CoachProfileRepository;
import com.impulse.domain.coachprofile.CoachProfile;
import com.impulse.domain.coachprofile.CoachProfileId;
import com.impulse.domain.coachprofile.CoachProfileDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetCoachProfileByIdUseCase {
    private final CoachProfileRepository coachProfileRepository;
    private final CoachProfileAppMapper coachProfileMapper;
    public GetCoachProfileByIdUseCase(CoachProfileRepository coachProfileRepository, CoachProfileAppMapper coachProfileMapper) {
        this.coachProfileRepository = coachProfileRepository;
        this.coachProfileMapper = coachProfileMapper;
    }
    public CoachProfileResponse execute(String coachProfileId) {
        if (coachProfileId == null || coachProfileId.trim().isEmpty()) throw new CoachProfileDomainError("CoachProfile ID cannot be null or empty");
        CoachProfileId id = CoachProfileId.of(coachProfileId);
        CoachProfile coachProfile = coachProfileRepository.findById(id)
            .orElseThrow(() -> new CoachProfileDomainError("CoachProfile not found with ID: " + coachProfileId));
        return coachProfileMapper.toResponse(coachProfile);
    }
}
