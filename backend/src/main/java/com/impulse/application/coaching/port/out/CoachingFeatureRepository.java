package com.impulse.application.coaching.port.out;

import com.impulse.domain.coaching.CoachingFeature;

import java.util.List;
import java.util.Optional;

public interface CoachingFeatureRepository {
    Optional<CoachingFeature> findById(String id);
    List<CoachingFeature> findAll();
    List<CoachingFeature> findByTier(String tier);
    List<CoachingFeature> findByType(String type);
    CoachingFeature save(CoachingFeature coachingFeature);
    void deleteById(String id);
}
