package com.impulse.infrastructure.persistence;

import com.impulse.domain.model.Coach;
import com.impulse.domain.model.enums.CoachStatus;
import com.impulse.domain.model.enums.CoachSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para gestión de Coaches
 */
@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {

    // ===== BÚSQUEDAS BÁSICAS =====
    
    Optional<Coach> findByUserId(Long userId);
    
    List<Coach> findByStatus(CoachStatus status);
    
    Page<Coach> findByStatus(CoachStatus status, Pageable pageable);
    
    List<Coach> findBySpecialtyIn(List<CoachSpecialty> specialties);
    
    Page<Coach> findBySpecialtyIn(List<CoachSpecialty> specialties, Pageable pageable);
    
    // ===== BÚSQUEDAS POR DISPONIBILIDAD =====
    
    @Query("SELECT c FROM Coach c WHERE c.status = 'ACTIVE' AND c.isAvailableForNewClients = true")
    List<Coach> findAvailableCoaches();
    
    @Query("SELECT c FROM Coach c WHERE c.status = 'ACTIVE' AND c.isAvailableForNewClients = true")
    Page<Coach> findAvailableCoaches(Pageable pageable);
    
    @Query("SELECT c FROM Coach c WHERE c.specialty = :specialty AND c.status = 'ACTIVE' AND c.isAvailableForNewClients = true")
    List<Coach> findAvailableCoachesBySpecialty(@Param("specialty") CoachSpecialty specialty);
    
    @Query("SELECT c FROM Coach c WHERE c.specialty = :specialty AND c.status = 'ACTIVE' AND c.isAvailableForNewClients = true")
    Page<Coach> findAvailableCoachesBySpecialty(@Param("specialty") CoachSpecialty specialty, Pageable pageable);
    
    // ===== BÚSQUEDAS POR RATING Y EXPERIENCIA =====
    
    @Query("SELECT c FROM Coach c WHERE c.averageRating >= :minRating ORDER BY c.averageRating DESC")
    List<Coach> findByMinimumRating(@Param("minRating") Double minRating);
    
    @Query("SELECT c FROM Coach c WHERE c.averageRating >= :minRating ORDER BY c.averageRating DESC")
    Page<Coach> findByMinimumRating(@Param("minRating") Double minRating, Pageable pageable);
    
    @Query("SELECT c FROM Coach c WHERE c.yearsOfExperience >= :minYears ORDER BY c.yearsOfExperience DESC")
    List<Coach> findByMinimumExperience(@Param("minYears") Integer minYears);
    
    @Query("SELECT c FROM Coach c WHERE c.yearsOfExperience >= :minYears ORDER BY c.yearsOfExperience DESC")
    Page<Coach> findByMinimumExperience(@Param("minYears") Integer minYears, Pageable pageable);
    
    // ===== TOP COACHES =====
    
    @Query("SELECT c FROM Coach c WHERE c.status = 'ACTIVE' ORDER BY c.averageRating DESC, c.totalSessions DESC")
    List<Coach> findTopCoaches(Pageable pageable);
    
    @Query("SELECT c FROM Coach c WHERE c.status = 'ACTIVE' AND c.specialty = :specialty ORDER BY c.averageRating DESC, c.totalSessions DESC")
    List<Coach> findTopCoachesBySpecialty(@Param("specialty") CoachSpecialty specialty, Pageable pageable);
    
    @Query("SELECT c FROM Coach c WHERE c.status = 'ACTIVE' ORDER BY c.totalSessions DESC")
    List<Coach> findMostActiveCoaches(Pageable pageable);
    
    // ===== ESTADÍSTICAS GLOBALES =====
    
    @Query("SELECT COUNT(c) FROM Coach c WHERE c.status = :status")
    Long countByStatus(@Param("status") CoachStatus status);
    
    @Query("SELECT COUNT(c) FROM Coach c WHERE c.specialty = :specialty AND c.status = 'ACTIVE'")
    Long countActiveBySpecialty(@Param("specialty") CoachSpecialty specialty);
    
    @Query("SELECT AVG(c.averageRating) FROM Coach c WHERE c.status = 'ACTIVE' AND c.totalSessions > 0")
    Double getAverageRatingAcrossAllCoaches();
    
    @Query("SELECT AVG(c.yearsOfExperience) FROM Coach c WHERE c.status = 'ACTIVE'")
    Double getAverageExperienceAcrossAllCoaches();
    
    // ===== ESTADÍSTICAS POR COACH =====
    
    @Query("SELECT SUM(c.totalSessions) FROM Coach c WHERE c.status = 'ACTIVE'")
    Long getTotalSessionsAcrossAllCoaches();
    
    @Query("SELECT c.totalSessions FROM Coach c WHERE c.id = :coachId")
    Integer getTotalSessionsByCoach(@Param("coachId") Long coachId);
    
    @Query("SELECT c.averageRating FROM Coach c WHERE c.id = :coachId")
    Double getAverageRatingByCoach(@Param("coachId") Long coachId);
    
    // ===== BÚSQUEDAS POR FECHAS =====
    
    @Query("SELECT c FROM Coach c WHERE c.user.createdAt >= :startDate")
    List<Coach> findCoachesJoinedAfter(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT c FROM Coach c WHERE c.user.createdAt BETWEEN :startDate AND :endDate")
    List<Coach> findCoachesJoinedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT c FROM Coach c WHERE c.user.lastLoginAt >= :since")
    List<Coach> findActiveCoachesSince(@Param("since") LocalDateTime since);
    
    // ===== BÚSQUEDAS DE COMPATIBILIDAD =====
    
    @Query("SELECT c FROM Coach c WHERE c.status = 'ACTIVE' AND c.isAvailableForNewClients = true " +
           "AND (:preferredLanguage IS NULL OR :preferredLanguage MEMBER OF c.languages) " +
           "AND (:specialty IS NULL OR c.specialty = :specialty) " +
           "AND (:minRating IS NULL OR c.averageRating >= :minRating)")
    List<Coach> findCompatibleCoaches(@Param("preferredLanguage") String preferredLanguage, 
                                     @Param("specialty") CoachSpecialty specialty,
                                     @Param("minRating") Double minRating);
    
    @Query("SELECT c FROM Coach c WHERE c.status = 'ACTIVE' AND c.isAvailableForNewClients = true " +
           "AND (:preferredLanguage IS NULL OR :preferredLanguage MEMBER OF c.languages) " +
           "AND (:specialty IS NULL OR c.specialty = :specialty) " +
           "AND (:minRating IS NULL OR c.averageRating >= :minRating)")
    Page<Coach> findCompatibleCoaches(@Param("preferredLanguage") String preferredLanguage, 
                                     @Param("specialty") CoachSpecialty specialty,
                                     @Param("minRating") Double minRating,
                                     Pageable pageable);
    
    // ===== BÚSQUEDAS POR CERTIFICACIONES =====
    
    @Query("SELECT c FROM Coach c WHERE :certification MEMBER OF c.certifications")
    List<Coach> findByCertification(@Param("certification") String certification);
    
    @Query("SELECT c FROM Coach c WHERE SIZE(c.certifications) >= :minCount")
    List<Coach> findByMinimumCertifications(@Param("minCount") Integer minCount);
    
    // ===== REPORTES Y ANALYTICS =====
    
    @Query("SELECT c.specialty, COUNT(c) FROM Coach c WHERE c.status = 'ACTIVE' GROUP BY c.specialty")
    List<Object[]> getCoachDistributionBySpecialty();
    
    @Query("SELECT FUNCTION('YEAR', c.user.createdAt) as year, FUNCTION('MONTH', c.user.createdAt) as month, COUNT(c) " +
           "FROM Coach c GROUP BY FUNCTION('YEAR', c.user.createdAt), FUNCTION('MONTH', c.user.createdAt) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getCoachRegistrationTrends();
    
    @Query("SELECT c FROM Coach c WHERE c.averageRating = (SELECT MAX(c2.averageRating) FROM Coach c2 WHERE c2.status = 'ACTIVE')")
    List<Coach> findTopRatedCoaches();
    
    @Query("SELECT c FROM Coach c WHERE c.totalSessions = (SELECT MAX(c2.totalSessions) FROM Coach c2 WHERE c2.status = 'ACTIVE')")
    List<Coach> findMostExperiencedCoaches();
    
    // ===== VALIDACIONES Y VERIFICACIONES =====
    
    @Query("SELECT c FROM Coach c WHERE c.isVerified = true AND c.status = 'ACTIVE'")
    List<Coach> findVerifiedCoaches();
    
    @Query("SELECT c FROM Coach c WHERE c.isVerified = true AND c.status = 'ACTIVE'")
    Page<Coach> findVerifiedCoaches(Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Coach c WHERE c.isVerified = true AND c.status = 'ACTIVE'")
    Long countVerifiedCoaches();
    
    // ===== BÚSQUEDAS AVANZADAS =====
    
    @Query("SELECT c FROM Coach c WHERE " +
           "(:query IS NULL OR " +
           "LOWER(c.user.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.user.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.bio) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.approachDescription) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Coach> searchCoaches(@Param("query") String query);
    
    @Query("SELECT c FROM Coach c WHERE " +
           "(:query IS NULL OR " +
           "LOWER(c.user.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.user.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.bio) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.approachDescription) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Coach> searchCoaches(@Param("query") String query, Pageable pageable);
    
    // ===== OPERACIONES DE MANTENIMIENTO =====
    
    @Query("SELECT c FROM Coach c WHERE c.status = 'INACTIVE' AND c.user.lastLoginAt < :cutoffDate")
    List<Coach> findInactiveCoachesOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("UPDATE Coach c SET c.status = 'INACTIVE' WHERE c.user.lastLoginAt < :cutoffDate")
    int deactivateInactiveCoaches(@Param("cutoffDate") LocalDateTime cutoffDate);
}
