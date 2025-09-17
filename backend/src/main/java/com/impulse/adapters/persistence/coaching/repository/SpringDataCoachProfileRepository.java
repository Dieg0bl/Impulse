package com.impulse.adapters.persistence.coaching.repository;

import com.impulse.adapters.persistence.coaching.entity.CoachProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCoachProfileRepository extends JpaRepository<CoachProfileJpaEntity, UUID> {

    Optional<CoachProfileJpaEntity> findByUserId(UUID userId);

    List<CoachProfileJpaEntity> findByIsActiveTrue();

    @Query("SELECT c FROM CoachProfileJpaEntity c WHERE c.avgRating >= :minRating AND c.isActive = true")
    List<CoachProfileJpaEntity> findByMinRatingAndActive(@Param("minRating") BigDecimal minRating);

    @Query("SELECT c FROM CoachProfileJpaEntity c WHERE c.specializations LIKE %:specialization% AND c.isActive = true")
    List<CoachProfileJpaEntity> findBySpecializationAndActive(@Param("specialization") String specialization);

    @Query("SELECT c FROM CoachProfileJpaEntity c WHERE c.hourlyRate <= :maxRate AND c.isActive = true")
    List<CoachProfileJpaEntity> findByMaxRateAndActive(@Param("maxRate") BigDecimal maxRate);

    @Query("SELECT COUNT(c) FROM CoachProfileJpaEntity c WHERE c.isActive = true")
    long countActiveCoaches();
}
