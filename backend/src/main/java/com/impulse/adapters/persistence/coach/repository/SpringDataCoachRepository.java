package com.impulse.adapters.persistence.coach.repository;

import com.impulse.adapters.persistence.coach.entity.CoachJpaEntity;
import com.impulse.domain.enums.CoachStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCoachRepository extends JpaRepository<CoachJpaEntity, UUID> {

    Optional<CoachJpaEntity> findByUserId(UUID userId);

    List<CoachJpaEntity> findByStatus(CoachStatus status);

    List<CoachJpaEntity> findByIsVerifiedTrue();

    @Query("SELECT c FROM CoachJpaEntity c WHERE c.averageRating >= :minRating AND c.status = :status")
    List<CoachJpaEntity> findByMinRatingAndStatus(@Param("minRating") BigDecimal minRating, @Param("status") CoachStatus status);

    @Query("SELECT c FROM CoachJpaEntity c WHERE c.specializations LIKE %:specialization% AND c.status = :status")
    List<CoachJpaEntity> findBySpecializationAndStatus(@Param("specialization") String specialization, @Param("status") CoachStatus status);

    @Query("SELECT c FROM CoachJpaEntity c WHERE c.hourlyRate <= :maxRate AND c.status = :status")
    List<CoachJpaEntity> findByMaxRateAndStatus(@Param("maxRate") BigDecimal maxRate, @Param("status") CoachStatus status);

    long countByStatus(CoachStatus status);

    @Query("SELECT AVG(c.averageRating) FROM CoachJpaEntity c WHERE c.status = 'ACTIVE'")
    BigDecimal getAverageRatingOfActiveCoaches();
}


