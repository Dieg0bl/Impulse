package com.impulse.application.coachprofile.port;

import com.impulse.domain.coachprofile.CoachProfile;
import com.impulse.domain.coachprofile.CoachProfileId;
import java.util.Optional;

public interface CoachProfileRepository {
    CoachProfile save(CoachProfile coachProfile);
    Optional<CoachProfile> findById(CoachProfileId id);
    long countByIsActive(Boolean isActive);
}
