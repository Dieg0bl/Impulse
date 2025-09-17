package com.impulse.application.coaching.port.in;

import com.impulse.domain.coaching.CoachingTier;

import java.util.List;
import java.util.Optional;

public interface GetCoachingTierQuery {
    Optional<CoachingTier> findByTier(String tier);
    List<CoachingTier> findAll();
    List<CoachingTier> findByPriceRange(double minPrice, double maxPrice);
}
