package com.impulse.lean.repository;

import java.time.LocalDateTime;
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
import com.impulse.user.model.User;
import com.impulse.lean.domain.model.UserAchievement;

/**
 * IMPULSE LEAN v1 - User Achievement Repository
 * 
 * Repository for UserAchievement entity
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    List<UserAchievement> findByUser(User user);

    List<UserAchievement> findByUserOrderByEarnedAtDesc(User user);

    Page<UserAchievement> findByUserOrderByEarnedAtDesc(User user, Pageable pageable);

    Optional<UserAchievement> findByUserAndAchievement(User user, Achievement achievement);

    List<UserAchievement> findByUserAndAchievementCategory(User user, AchievementCategory category);

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user = :user AND ua.achievement.category = :category ORDER BY ua.earnedAt DESC")
    List<UserAchievement> findByUserAndCategory(@Param("user") User user, @Param("category") AchievementCategory category);

    @Query("SELECT COUNT(ua) FROM UserAchievement ua WHERE ua.user = :user")
    Long countByUser(@Param("user") User user);

    @Query("SELECT SUM(ua.earnedCount * ua.achievement.points) FROM UserAchievement ua WHERE ua.user = :user")
    Long getTotalPointsByUser(@Param("user") User user);

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user = :user AND ua.earnedAt >= :startDate ORDER BY ua.earnedAt DESC")
    List<UserAchievement> findRecentAchievements(@Param("user") User user, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.isNotified = false")
    List<UserAchievement> findUnnotifiedAchievements();

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user = :user AND ua.isNotified = false ORDER BY ua.earnedAt DESC")
    List<UserAchievement> findUnnotifiedByUser(@Param("user") User user);

    @Query("SELECT COUNT(DISTINCT ua.user) FROM UserAchievement ua WHERE ua.achievement = :achievement")
    Long countUsersWithAchievement(@Param("achievement") Achievement achievement);

    @Query("SELECT ua.achievement.category, COUNT(ua) FROM UserAchievement ua WHERE ua.user = :user GROUP BY ua.achievement.category")
    List<Object[]> getAchievementCountByCategory(@Param("user") User user);

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user = :user AND ua.achievement.level = :level ORDER BY ua.earnedAt DESC")
    List<UserAchievement> findByUserAndLevel(@Param("user") User user, @Param("level") String level);

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.earnedAt BETWEEN :startDate AND :endDate ORDER BY ua.earnedAt DESC")
    List<UserAchievement> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DISTINCT ua.user FROM UserAchievement ua WHERE ua.achievement = :achievement")
    List<User> findUsersWithAchievement(@Param("achievement") Achievement achievement);

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user = :user AND ua.earnedCount > 1 ORDER BY ua.earnedCount DESC")
    List<UserAchievement> findMultipleEarnedByUser(@Param("user") User user);

    @Query("SELECT AVG(ua.earnedCount) FROM UserAchievement ua WHERE ua.achievement = :achievement")
    Double getAverageEarnCountForAchievement(@Param("achievement") Achievement achievement);

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.sourceType = :sourceType AND ua.sourceId = :sourceId")
    List<UserAchievement> findBySource(@Param("sourceType") String sourceType, @Param("sourceId") Long sourceId);

    @Query("SELECT COUNT(ua) FROM UserAchievement ua WHERE ua.user = :user AND ua.achievement.category = :category")
    Long countByUserAndCategory(@Param("user") User user, @Param("category") AchievementCategory category);

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user.id IN :userIds ORDER BY ua.earnedAt DESC")
    List<UserAchievement> findByUserIds(@Param("userIds") List<Long> userIds);

    @Query("SELECT ua.user, COUNT(ua) as total FROM UserAchievement ua GROUP BY ua.user ORDER BY total DESC")
    List<Object[]> getLeaderboard(Pageable pageable);

    @Query("SELECT ua.user, SUM(ua.earnedCount * ua.achievement.points) as totalPoints FROM UserAchievement ua GROUP BY ua.user ORDER BY totalPoints DESC")
    List<Object[]> getPointsLeaderboard(Pageable pageable);

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user = :user AND ua.context LIKE %:contextType%")
    List<UserAchievement> findByUserAndContextType(@Param("user") User user, @Param("contextType") String contextType);

    boolean existsByUserAndAchievement(User user, Achievement achievement);

    @Query("SELECT COUNT(ua) FROM UserAchievement ua WHERE ua.achievement.category = :category AND ua.earnedAt >= :startDate")
    Long countByCategoryAndDateAfter(@Param("category") AchievementCategory category, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user = :user ORDER BY ua.achievement.points DESC")
    List<UserAchievement> findByUserOrderByPoints(@Param("user") User user);
}
