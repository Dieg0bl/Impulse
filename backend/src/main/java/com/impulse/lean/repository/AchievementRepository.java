package com.impulse.lean.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.impulse.lean.domain.model.Achievement;
import com.impulse.lean.domain.model.AchievementCategory;
import com.impulse.lean.domain.model.AchievementLevel;
import com.impulse.lean.domain.model.AchievementStatus;

/**
 * IMPULSE LEAN v1 - Achievement Repository
 * 
 * Repository for Achievement entity
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    Optional<Achievement> findByCode(String code);

    List<Achievement> findByStatus(AchievementStatus status);

    List<Achievement> findByCategory(AchievementCategory category);

    List<Achievement> findByLevel(AchievementLevel level);

    List<Achievement> findByStatusAndCategory(AchievementStatus status, AchievementCategory category);

    Page<Achievement> findByStatusOrderByLevelAscPointsAsc(AchievementStatus status, Pageable pageable);

    @Query("SELECT a FROM Achievement a WHERE a.status = :status AND a.isActive = true ORDER BY a.category, a.level, a.points")
    List<Achievement> findActiveAchievements(@Param("status") AchievementStatus status);

    @Query("SELECT a FROM Achievement a WHERE a.isActive = true AND a.status = 'ACTIVE' AND a.category = :category ORDER BY a.level, a.points")
    List<Achievement> findAvailableByCategory(@Param("category") AchievementCategory category);

    @Query("SELECT COUNT(a) FROM Achievement a WHERE a.status = :status")
    Long countByStatus(@Param("status") AchievementStatus status);

    @Query("SELECT a FROM Achievement a WHERE a.isActive = true AND a.status = 'ACTIVE' AND " +
           "(LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Achievement> searchAchievements(@Param("searchTerm") String searchTerm);

    @Query("SELECT a FROM Achievement a WHERE a.isActive = true AND a.status = 'ACTIVE' AND " +
           "a.points BETWEEN :minPoints AND :maxPoints ORDER BY a.points")
    List<Achievement> findByPointsRange(@Param("minPoints") Integer minPoints, @Param("maxPoints") Integer maxPoints);

    @Query("SELECT DISTINCT a.category FROM Achievement a WHERE a.isActive = true AND a.status = 'ACTIVE'")
    List<AchievementCategory> findAvailableCategories();

    @Query("SELECT a FROM Achievement a WHERE a.isActive = true AND a.status = 'ACTIVE' AND " +
           "a.level IN :levels ORDER BY a.category, a.points")
    List<Achievement> findByLevels(@Param("levels") List<AchievementLevel> levels);

    @Query("SELECT COUNT(a) FROM Achievement a WHERE a.category = :category AND a.status = 'ACTIVE'")
    Long countActiveByCategory(@Param("category") AchievementCategory category);

    @Query("SELECT a FROM Achievement a WHERE a.isActive = true AND a.status = 'ACTIVE' AND " +
           "a.criteriaType = :criteriaType ORDER BY a.level, a.points")
    List<Achievement> findByCriteriaType(@Param("criteriaType") String criteriaType);

    @Query("SELECT a FROM Achievement a WHERE a.isActive = true AND a.status = 'ACTIVE' AND " +
           "a.criteriaValue <= :currentValue AND a.criteriaType = :criteriaType")
    List<Achievement> findEligibleAchievements(@Param("criteriaType") String criteriaType, 
                                              @Param("currentValue") Integer currentValue);

    @Query("SELECT a FROM Achievement a WHERE a.badgeIcon IS NOT NULL AND a.isActive = true ORDER BY a.level")
    List<Achievement> findAchievementsWithBadges();

    @Query("SELECT AVG(a.points) FROM Achievement a WHERE a.category = :category AND a.status = 'ACTIVE'")
    Double getAveragePointsByCategory(@Param("category") AchievementCategory category);

    @Query("SELECT a FROM Achievement a WHERE a.id IN :ids")
    List<Achievement> findByIds(@Param("ids") List<Long> ids);

    boolean existsByCode(String code);

    @Query("SELECT a FROM Achievement a WHERE a.isActive = true AND a.status = 'ACTIVE' AND " +
           "a.targetAudience = :audience ORDER BY a.level, a.points")
    List<Achievement> findByTargetAudience(@Param("audience") String audience);
}
