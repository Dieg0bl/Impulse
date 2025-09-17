package com.impulse.adapters.persistence.coaching;

import com.impulse.domain.coaching.CoachingFeature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachingFeatureRepository extends JpaRepository<CoachingFeature, String> {
}
