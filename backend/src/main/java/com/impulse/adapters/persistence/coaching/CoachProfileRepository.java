package com.impulse.adapters.persistence.coaching;

import com.impulse.domain.coachprofile.CoachProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachProfileRepository extends JpaRepository<CoachProfile, String> {
}


