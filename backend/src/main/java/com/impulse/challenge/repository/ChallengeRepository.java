package com.impulse.challenge.repository;

import com.impulse.challenge.model.Challenge;
import com.impulse.challenge.model.ChallengeCategory;
import com.impulse.challenge.model.ChallengeDifficulty;
import com.impulse.challenge.model.ChallengeStatus;
import com.impulse.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * IMPULSE LEAN v1 - Challenge Repository Interface
 * 
 * Repository for challenge domain entity operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    // Basic lookups
    Optional<Challenge> findByUuid(String uuid);
    Optional<Challenge> findBySlug(String slug);
    
    boolean existsBySlug(String slug);

    // Status queries
    List<Challenge> findByStatus(ChallengeStatus status);
    Page<Challenge> findByStatusOrderByCreatedAtDesc(ChallengeStatus status, Pageable pageable);

    // Active challenges
    @Query("SELECT c FROM Challenge c WHERE c.status = 'ACTIVE'")
    List<Challenge> findActiveChallenges();

    @Query("SELECT c FROM Challenge c WHERE c.status = 'ACTIVE' " +
           "AND (c.endDate IS NULL OR c.endDate > :now)")
    List<Challenge> findActiveAndNotExpiredChallenges(@Param("now") LocalDateTime now);

    // Category and difficulty
    List<Challenge> findByCategory(ChallengeCategory category);
    List<Challenge> findByDifficulty(ChallengeDifficulty difficulty);
    
    @Query("SELECT c FROM Challenge c WHERE c.category = :category AND c.status = 'ACTIVE'")
    List<Challenge> findActiveChallengesByCategory(@Param("category") ChallengeCategory category);

    @Query("SELECT c FROM Challenge c WHERE c.difficulty = :difficulty AND c.status = 'ACTIVE'")
    List<Challenge> findActiveChallengesByDifficulty(@Param("difficulty") ChallengeDifficulty difficulty);

    // Creator queries
    List<Challenge> findByCreator(User creator);
    
    @Query("SELECT c FROM Challenge c WHERE c.creator = :creator ORDER BY c.createdAt DESC")
    Page<Challenge> findByCreatorOrderByCreatedAtDesc(@Param("creator") User creator, Pageable pageable);

    // Time-based queries
    @Query("SELECT c FROM Challenge c WHERE c.createdAt >= :since")
    List<Challenge> findChallengesCreatedAfter(@Param("since") LocalDateTime since);

    @Query("SELECT c FROM Challenge c WHERE c.startDate <= :now AND " +
           "(c.endDate IS NULL OR c.endDate >= :now) AND c.status = 'ACTIVE'")
    List<Challenge> findCurrentlyActiveChallenges(@Param("now") LocalDateTime now);

    @Query("SELECT c FROM Challenge c WHERE c.endDate < :now AND c.status = 'ACTIVE'")
    List<Challenge> findExpiredActiveChallenges(@Param("now") LocalDateTime now);

    // Search capabilities
    @Query("SELECT c FROM Challenge c WHERE " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.slug) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Challenge> searchChallenges(@Param("search") String searchTerm, Pageable pageable);

    @Query("SELECT c FROM Challenge c WHERE " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND c.status = 'ACTIVE'")
    Page<Challenge> searchActiveChallenges(@Param("search") String searchTerm, Pageable pageable);

    // Statistics queries
    @Query("SELECT COUNT(c) FROM Challenge c WHERE c.status = :status")
    long countByStatus(@Param("status") ChallengeStatus status);

    @Query("SELECT c.category, COUNT(c) FROM Challenge c WHERE c.status = 'ACTIVE' GROUP BY c.category")
    List<Object[]> countActiveChallengesByCategory();

    @Query("SELECT c.difficulty, COUNT(c) FROM Challenge c WHERE c.status = 'ACTIVE' GROUP BY c.difficulty")
    List<Object[]> countActiveChallengesByDifficulty();

    // Participation queries
    @Query("SELECT c FROM Challenge c WHERE SIZE(c.participations) > :minParticipants")
    List<Challenge> findChallengesWithMinimumParticipants(@Param("minParticipants") int minParticipants);

    @Query("SELECT c FROM Challenge c WHERE c.status = 'ACTIVE' " +
           "ORDER BY SIZE(c.participations) DESC")
    List<Challenge> findMostPopularActiveChallenges(Pageable pageable);

    @Query("SELECT c FROM Challenge c WHERE c.status = 'ACTIVE' " +
           "AND SIZE(c.participations) < c.maxParticipants")
    List<Challenge> findAvailableChallenges();

    // Featured and recommended
    @Query("SELECT c FROM Challenge c WHERE c.featured = true AND c.status = 'ACTIVE'")
    List<Challenge> findFeaturedChallenges();

    @Query("SELECT c FROM Challenge c WHERE c.status = 'ACTIVE' " +
           "AND c.category = :category " +
           "ORDER BY SIZE(c.participations) DESC, c.createdAt DESC")
    List<Challenge> findRecommendedChallengesByCategory(@Param("category") ChallengeCategory category, 
                                                        Pageable pageable);

    // Complex business queries
    @Query("SELECT c FROM Challenge c WHERE c.status = 'ACTIVE' " +
           "AND c.creator != :user " +
           "AND c.id NOT IN (SELECT p.challenge.id FROM ChallengeParticipation p WHERE p.user = :user)")
    List<Challenge> findAvailableChallengesForUser(@Param("user") User user);

    @Query("SELECT c FROM Challenge c WHERE c.status = 'ACTIVE' " +
           "AND c.difficulty = :difficulty " +
           "AND c.category IN :categories " +
           "ORDER BY c.createdAt DESC")
    List<Challenge> findChallengesByDifficultyAndCategories(@Param("difficulty") ChallengeDifficulty difficulty,
                                                           @Param("categories") List<ChallengeCategory> categories,
                                                           Pageable pageable);
}
