package com.impulse.adapters.persistence.coaching;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachingFeatureJpaRepository extends JpaRepository<CoachingFeatureEntity, String> {

    @Query("SELECT cf FROM CoachingFeatureEntity cf WHERE cf.tier = :tier")
    List<CoachingFeatureEntity> findByTier(@Param("tier") String tier);

    @Query("SELECT cf FROM CoachingFeatureEntity cf WHERE cf.type = :type")
    List<CoachingFeatureEntity> findByType(@Param("type") String type);
}
