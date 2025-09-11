package com.impulse.lean.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.model.UserStatistics;

/**
 * IMPULSE LEAN v1 - User Statistics Repository
 * 
 * Repository for UserStatistics entity
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatistics, Long> {

    Optional<UserStatistics> findByUser(User user);

    Optional<UserStatistics> findByUserId(Long userId);

    @Query("SELECT us FROM UserStatistics us WHERE us.uciScore >= :minScore ORDER BY us.uciScore DESC")
    List<UserStatistics> findByUciScoreGreaterThanEqual(@Param("minScore") BigDecimal minScore);

    @Query("SELECT us FROM UserStatistics us ORDER BY us.uciScore DESC")
    Page<UserStatistics> findAllOrderByUciScoreDesc(Pageable pageable);

    @Query("SELECT us FROM UserStatistics us ORDER BY us.totalPoints DESC")
    Page<UserStatistics> findAllOrderByTotalPointsDesc(Pageable pageable);

    @Query("SELECT us FROM UserStatistics us WHERE us.challengesCompleted >= :minCompleted ORDER BY us.challengesCompleted DESC")
    List<UserStatistics> findByMinChallengesCompleted(@Param("minCompleted") Integer minCompleted);

    @Query("SELECT AVG(us.uciScore) FROM UserStatistics us WHERE us.uciScore > 0")
    BigDecimal getAverageUciScore();

    @Query("SELECT AVG(us.totalPoints) FROM UserStatistics us WHERE us.totalPoints > 0")
    Double getAverageTotalPoints();

    @Query("SELECT us FROM UserStatistics us WHERE us.currentLevel >= :minLevel ORDER BY us.currentLevel DESC, us.levelPoints DESC")
    List<UserStatistics> findByMinLevel(@Param("minLevel") Integer minLevel);

    @Query("SELECT COUNT(us) FROM UserStatistics us WHERE us.currentLevel = :level")
    Long countByLevel(@Param("level") Integer level);

    @Query("SELECT us FROM UserStatistics us WHERE us.currentStreak >= :minStreak ORDER BY us.currentStreak DESC")
    List<UserStatistics> findByMinStreak(@Param("minStreak") Integer minStreak);

    @Query("SELECT us FROM UserStatistics us WHERE us.lastActivity >= :since ORDER BY us.lastActivity DESC")
    List<UserStatistics> findActiveUsersSince(@Param("since") LocalDateTime since);

    @Query("SELECT us FROM UserStatistics us WHERE us.lastActivity < :before")
    List<UserStatistics> findInactiveUsersBefore(@Param("before") LocalDateTime before);

    @Query("SELECT us FROM UserStatistics us WHERE us.successRate >= :minRate ORDER BY us.successRate DESC")
    List<UserStatistics> findByMinSuccessRate(@Param("minRate") BigDecimal minRate);

    @Query("SELECT us.currentLevel, COUNT(us) FROM UserStatistics us GROUP BY us.currentLevel ORDER BY us.currentLevel")
    List<Object[]> getLevelDistribution();

    @Query("SELECT us FROM UserStatistics us WHERE us.totalAchievements >= :minAchievements ORDER BY us.totalAchievements DESC")
    List<UserStatistics> findByMinAchievements(@Param("minAchievements") Integer minAchievements);

    @Query("SELECT us FROM UserStatistics us WHERE us.engagementRate >= :minRate ORDER BY us.engagementRate DESC")
    List<UserStatistics> findByMinEngagementRate(@Param("minRate") BigDecimal minRate);

    @Query("SELECT MAX(us.currentStreak) FROM UserStatistics us")
    Integer getMaxStreak();

    @Query("SELECT MAX(us.longestStreak) FROM UserStatistics us")
    Integer getMaxLongestStreak();

    @Query("SELECT MAX(us.uciScore) FROM UserStatistics us")
    BigDecimal getMaxUciScore();

    @Query("SELECT us FROM UserStatistics us WHERE us.socialScore >= :minScore ORDER BY us.socialScore DESC")
    List<UserStatistics> findByMinSocialScore(@Param("minScore") BigDecimal minScore);

    @Query("SELECT us FROM UserStatistics us WHERE us.evidenceSubmitted >= :minEvidence ORDER BY us.evidenceSubmitted DESC")
    List<UserStatistics> findByMinEvidenceSubmitted(@Param("minEvidence") Integer minEvidence);

    @Query("SELECT us FROM UserStatistics us WHERE us.validationsPerformed >= :minValidations ORDER BY us.validationsPerformed DESC")
    List<UserStatistics> findByMinValidationsPerformed(@Param("minValidations") Integer minValidations);

    @Query("SELECT AVG(us.successRate) FROM UserStatistics us WHERE us.challengesAttempted > 0")
    BigDecimal getAverageSuccessRate();

    @Query("SELECT AVG(us.engagementRate) FROM UserStatistics us WHERE us.engagementRate > 0")
    BigDecimal getAverageEngagementRate();

    @Query("SELECT us FROM UserStatistics us WHERE us.user.id IN :userIds")
    List<UserStatistics> findByUserIds(@Param("userIds") List<Long> userIds);

    @Query("SELECT COUNT(us) FROM UserStatistics us WHERE us.uciScore >= :score")
    Long countByUciScoreGreaterThanEqual(@Param("score") BigDecimal score);

    @Query("SELECT us FROM UserStatistics us WHERE us.levelPoints >= :minPoints AND us.currentLevel = :level ORDER BY us.levelPoints DESC")
    List<UserStatistics> findByLevelAndMinPoints(@Param("level") Integer level, @Param("minPoints") Integer minPoints);

    @Query("SELECT us FROM UserStatistics us WHERE us.monthlyPoints >= :minPoints ORDER BY us.monthlyPoints DESC")
    List<UserStatistics> findByMinMonthlyPoints(@Param("minPoints") Integer minPoints);

    @Query("SELECT us FROM UserStatistics us WHERE us.weeklyPoints >= :minPoints ORDER BY us.weeklyPoints DESC")
    List<UserStatistics> findByMinWeeklyPoints(@Param("minPoints") Integer minPoints);

    @Query("SELECT us FROM UserStatistics us WHERE us.positiveValidationsReceived >= :minValidations ORDER BY us.positiveValidationsReceived DESC")
    List<UserStatistics> findByMinPositiveValidations(@Param("minValidations") Integer minValidations);

    @Query("SELECT us FROM UserStatistics us ORDER BY (us.positiveValidationsReceived * 1.0 / NULLIF(us.totalValidationsReceived, 0)) DESC")
    List<UserStatistics> findOrderByValidationRate();

    boolean existsByUser(User user);

    @Query("SELECT us FROM UserStatistics us WHERE us.totalPoints BETWEEN :minPoints AND :maxPoints ORDER BY us.totalPoints DESC")
    List<UserStatistics> findByPointsRange(@Param("minPoints") Long minPoints, @Param("maxPoints") Long maxPoints);
}
