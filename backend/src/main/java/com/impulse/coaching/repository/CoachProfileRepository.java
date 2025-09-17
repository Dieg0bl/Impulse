package com.impulse.coaching.repository;

import com.impulse.domain.model.CoachProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachProfileRepository extends JpaRepository<CoachProfile, String> {
}
