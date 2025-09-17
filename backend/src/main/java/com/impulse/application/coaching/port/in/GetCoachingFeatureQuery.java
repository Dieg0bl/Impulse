package com.impulse.application.coaching.port.in;

import com.impulse.domain.coaching.CoachingFeature;

import java.util.List;
import java.util.Optional;

public interface GetCoachingFeatureQuery {
    Optional<CoachingFeature> findById(String id);
    List<CoachingFeature> findAll();
    List<CoachingFeature> findByTier(String tier);
    List<CoachingFeature> findByType(String type);
}
