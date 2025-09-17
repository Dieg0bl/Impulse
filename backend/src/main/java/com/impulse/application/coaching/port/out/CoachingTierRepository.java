package com.impulse.application.coaching.port.out;

import com.impulse.domain.coaching.CoachingTier;

import java.util.List;
import java.util.Optional;

public interface CoachingTierRepository {
    Optional<CoachingTier> findByTier(String tier);
    List<CoachingTier> findAll();
    List<CoachingTier> findByPriceRange(double minPrice, double maxPrice);
    CoachingTier save(CoachingTier coachingTier);
    void deleteByTier(String tier);
}
