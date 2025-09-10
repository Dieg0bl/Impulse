package com.impulse.infrastructure.persistence.repositories;

import com.impulse.domain.entities.Challenge;
import com.impulse.domain.entities.Challenge.ChallengeDifficulty;
import com.impulse.domain.enums.ChallengeStatus;
import com.impulse.domain.enums.EvidenceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para Challenge
 */
@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    
    // Búsquedas básicas
    List<Challenge> findByCreatorId(Long creatorId);
    Page<Challenge> findByCreatorId(Long creatorId, Pageable pageable);
    
    // Búsquedas por status
    List<Challenge> findByStatus(ChallengeStatus status);
    Page<Challenge> findByStatus(ChallengeStatus status, Pageable pageable);
    
    List<Challenge> findByStatusIn(List<ChallengeStatus> statuses);
    Page<Challenge> findByStatusIn(List<ChallengeStatus> statuses, Pageable pageable);
    
    // Búsquedas por categoría
    List<Challenge> findByCategory(String category);
    Page<Challenge> findByCategory(String category, Pageable pageable);
    
    @Query("SELECT DISTINCT c.category FROM Challenge c WHERE c.publiclyVisible = true")
    List<String> findAllCategories();
    
    // Búsquedas por dificultad
    List<Challenge> findByDifficulty(ChallengeDifficulty difficulty);
    Page<Challenge> findByDifficulty(ChallengeDifficulty difficulty, Pageable pageable);
    
    // Búsquedas por fechas
    List<Challenge> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
    List<Challenge> findByEndDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT c FROM Challenge c WHERE c.startDate <= :now AND c.endDate >= :now AND c.status = 'ACTIVE'")
    List<Challenge> findCurrentlyActive(@Param("now") LocalDateTime now);
    
    @Query("SELECT c FROM Challenge c WHERE c.endDate < :now AND c.status = 'ACTIVE'")
    List<Challenge> findExpiredChallenges(@Param("now") LocalDateTime now);
    
    @Query("SELECT c FROM Challenge c WHERE c.startDate > :now AND c.status = 'ACTIVE'")
    List<Challenge> findUpcomingChallenges(@Param("now") LocalDateTime now);
    
    // Búsquedas por visibilidad y destacados
    List<Challenge> findByPubliclyVisible(Boolean publiclyVisible);
    Page<Challenge> findByPubliclyVisibleAndStatus(Boolean publiclyVisible, ChallengeStatus status, Pageable pageable);
    
    List<Challenge> findByFeatured(Boolean featured);
    Page<Challenge> findByFeaturedAndPubliclyVisibleAndStatus(Boolean featured, Boolean publiclyVisible, 
                                                            ChallengeStatus status, Pageable pageable);
    
    // Búsquedas por participación
    List<Challenge> findByParticipantCountGreaterThanEqual(Integer minParticipants);
    List<Challenge> findByParticipantCountLessThan(Integer maxParticipants);
    
    @Query("SELECT c FROM Challenge c WHERE c.participantCount < c.maxParticipants OR c.maxParticipants IS NULL")
    List<Challenge> findAvailableForJoining();
    
    // Búsquedas por coaching
    List<Challenge> findByAllowsCoaching(Boolean allowsCoaching);
    
    // Búsquedas por validación
    List<Challenge> findByRequiresValidation(Boolean requiresValidation);
    
    // Búsquedas por tipos de evidencia
    @Query("SELECT c FROM Challenge c JOIN c.allowedEvidenceTypes et WHERE et = :evidenceType")
    List<Challenge> findByAllowedEvidenceType(@Param("evidenceType") EvidenceType evidenceType);
    
    // Búsquedas por tags
    @Query("SELECT c FROM Challenge c JOIN c.tags t WHERE t IN :tags")
    List<Challenge> findByTagsIn(@Param("tags") List<String> tags);
    
    @Query("SELECT c FROM Challenge c JOIN c.tags t WHERE LOWER(t) LIKE LOWER(CONCAT('%', :tag, '%'))")
    List<Challenge> findByTagContaining(@Param("tag") String tag);
    
    // Búsqueda de texto completo
    @Query("SELECT c FROM Challenge c WHERE " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.category) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Challenge> searchChallenges(@Param("search") String search, Pageable pageable);
    
    // Challenges populares y trending
    @Query("SELECT c FROM Challenge c WHERE c.publiclyVisible = true AND c.status = 'ACTIVE' " +
           "ORDER BY c.participantCount DESC, c.viewCount DESC")
    Page<Challenge> findPopularChallenges(Pageable pageable);
    
    @Query("SELECT c FROM Challenge c WHERE c.publiclyVisible = true AND c.status = 'ACTIVE' " +
           "AND c.createdAt >= :since ORDER BY c.participantCount DESC")
    List<Challenge> findTrendingChallenges(@Param("since") LocalDateTime since);
    
    @Query("SELECT c FROM Challenge c WHERE c.publiclyVisible = true AND c.status = 'ACTIVE' " +
           "ORDER BY c.averageRating DESC, c.participantCount DESC")
    Page<Challenge> findTopRatedChallenges(Pageable pageable);
    
    // Recomendaciones
    @Query("SELECT c FROM Challenge c WHERE c.category = :category AND c.id != :excludeId " +
           "AND c.publiclyVisible = true AND c.status = 'ACTIVE' " +
           "ORDER BY c.averageRating DESC, c.participantCount DESC")
    List<Challenge> findSimilarChallenges(@Param("category") String category, 
                                        @Param("excludeId") Long excludeId, 
                                        Pageable pageable);
    
    // Estadísticas
    @Query("SELECT COUNT(c) FROM Challenge c WHERE c.status = :status")
    long countByStatus(@Param("status") ChallengeStatus status);
    
    @Query("SELECT COUNT(c) FROM Challenge c WHERE c.creatorId = :creatorId")
    long countByCreatorId(@Param("creatorId") Long creatorId);
    
    @Query("SELECT COUNT(c) FROM Challenge c WHERE c.category = :category")
    long countByCategory(@Param("category") String category);
    
    @Query("SELECT COUNT(c) FROM Challenge c WHERE c.createdAt >= :date")
    long countNewChallengesFromDate(@Param("date") LocalDateTime date);
    
    @Query("SELECT SUM(c.participantCount) FROM Challenge c WHERE c.status = 'ACTIVE'")
    Long getTotalActiveParticipants();
    
    @Query("SELECT AVG(c.participantCount) FROM Challenge c WHERE c.status = 'COMPLETED'")
    Double getAverageParticipantsForCompletedChallenges();
    
    @Query("SELECT AVG(c.completionRate) FROM Challenge c WHERE c.status = 'COMPLETED'")
    Double getAverageCompletionRate();
    
    // Métricas por periodo
    @Query("SELECT DATE(c.createdAt) as date, COUNT(c) as count FROM Challenge c " +
           "WHERE c.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(c.createdAt) ORDER BY date")
    List<Object[]> getChallengeCreationsByDay(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT c.category, COUNT(c) as count FROM Challenge c " +
           "WHERE c.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY c.category ORDER BY count DESC")
    List<Object[]> getChallengesByCategory(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    // Challenges para moderación
    @Query("SELECT c FROM Challenge c WHERE c.evidenceCount = 0 AND c.createdAt < :date")
    List<Challenge> findChallengesWithoutEvidences(@Param("date") LocalDateTime date);
    
    @Query("SELECT c FROM Challenge c WHERE c.participantCount = 0 AND c.createdAt < :date")
    List<Challenge> findChallengesWithoutParticipants(@Param("date") LocalDateTime date);
    
    // Ranking de creadores
    @Query("SELECT c.creatorId, COUNT(c) as challengeCount FROM Challenge c " +
           "WHERE c.status != 'DRAFT' GROUP BY c.creatorId ORDER BY challengeCount DESC")
    List<Object[]> getTopChallengeCreators(Pageable pageable);
    
    // Challenges por recompensas
    List<Challenge> findByRewardPointsGreaterThan(Integer minPoints);
    List<Challenge> findByMonetaryRewardGreaterThan(BigDecimal minReward);
    
    @Query("SELECT c FROM Challenge c WHERE c.monetaryReward IS NOT NULL AND c.monetaryReward > 0 " +
           "AND c.publiclyVisible = true ORDER BY c.monetaryReward DESC")
    Page<Challenge> findPaidChallenges(Pageable pageable);
    
    // Challenges por duración
    List<Challenge> findByDurationInDaysLessThanEqual(Integer maxDays);
    List<Challenge> findByDurationInDaysGreaterThanEqual(Integer minDays);
    
    // Metadata queries
    @Query("SELECT c FROM Challenge c JOIN c.metadata m WHERE KEY(m) = :key AND VALUE(m) = :value")
    List<Challenge> findByMetadata(@Param("key") String key, @Param("value") String value);
}
