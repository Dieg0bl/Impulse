package com.impulse.adapters.persistence.validator.repository;

import com.impulse.adapters.persistence.validator.entity.ValidatorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataValidatorRepository extends JpaRepository<ValidatorJpaEntity, UUID> {

    Optional<ValidatorJpaEntity> findByEmail(String email);

    List<ValidatorJpaEntity> findByIsActive(Boolean isActive);

    List<ValidatorJpaEntity> findByIsActiveTrueOrderByRatingDesc();

    @Query("SELECT v FROM ValidatorJpaEntity v WHERE v.specializations LIKE %:specialization% AND v.isActive = true")
    List<ValidatorJpaEntity> findBySpecializationAndActive(@Param("specialization") String specialization);

    @Query("SELECT v FROM ValidatorJpaEntity v WHERE v.rating >= :minRating AND v.isActive = true")
    List<ValidatorJpaEntity> findByMinRatingAndActive(@Param("minRating") Double minRating);

    @Query("SELECT v FROM ValidatorJpaEntity v WHERE v.experienceYears >= :minYears AND v.isActive = true")
    List<ValidatorJpaEntity> findByMinExperienceAndActive(@Param("minYears") Integer minYears);

    @Query("SELECT COUNT(v) FROM ValidatorJpaEntity v WHERE v.isActive = true")
    long countActiveValidators();

    @Query("SELECT AVG(v.rating) FROM ValidatorJpaEntity v WHERE v.isActive = true")
    Double getAverageRatingOfActiveValidators();
}
