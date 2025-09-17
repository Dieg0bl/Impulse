package com.impulse.adapters.persistence.coaching;

import com.impulse.domain.coaching.CoachingTier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachingTierRepository extends JpaRepository<CoachingTier, String> {
}

