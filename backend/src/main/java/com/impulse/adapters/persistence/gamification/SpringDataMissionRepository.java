package com.impulse.adapters.persistence.gamification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SpringDataMissionRepository extends JpaRepository<MissionJpaEntity, String> {

    List<MissionJpaEntity> findByIsActiveTrue();

    List<MissionJpaEntity> findByType(String type);

    List<MissionJpaEntity> findByCategory(String category);

    List<MissionJpaEntity> findByDifficulty(String difficulty);

    @Query("SELECT m FROM MissionJpaEntity m WHERE m.isActive = true AND m.startDate <= :now AND (m.endDate IS NULL OR m.endDate >= :now)")
    List<MissionJpaEntity> findActiveMissionsInDateRange(@Param("now") LocalDateTime now);

    @Query("SELECT m FROM MissionJpaEntity m WHERE m.type = :type AND m.isActive = true")
    Page<MissionJpaEntity> findActiveByType(@Param("type") String type, Pageable pageable);

    @Query("SELECT m FROM MissionJpaEntity m WHERE m.category = :category AND m.difficulty = :difficulty")
    List<MissionJpaEntity> findByCategoryAndDifficulty(@Param("category") String category, @Param("difficulty") String difficulty);
}
