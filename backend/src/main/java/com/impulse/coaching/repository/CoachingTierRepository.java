package com.impulse.coaching.repository;

import com.impulse.domain.model.CoachingTier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachingTierRepository extends JpaRepository<CoachingTier, String> {
}
