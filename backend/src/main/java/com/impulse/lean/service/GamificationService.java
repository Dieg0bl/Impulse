package com.impulse.lean.service;

import com.impulse.lean.domain.model.*;
import com.impulse.lean.repository.AchievementRepository;
import com.impulse.lean.repository.UserAchievementRepository;
import com.impulse.lean.repository.UserStatisticsRepository;
import com.impulse.lean.dto.response.AchievementResponse;
import com.impulse.lean.dto.response.LeaderboardResponse;
import com.impulse.lean.dto.response.UserProgressResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * IMPULSE LEAN v1 - Gamification Service
 * 
 * Core service for gamification system including achievements, user statistics,
 * UCI (User Commitment Index), and CPS (Challenge Priority Score) algorithms
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Service
@Transactional
public class GamificationService {

    private static final Logger logger = LoggerFactory.getLogger(GamificationService.class);

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    @Autowired
    private UserStatisticsRepository userStatisticsRepository;

    // UCI Algorithm Constants
    private static final BigDecimal UCI_CONSISTENCY_WEIGHT = new BigDecimal("0.4");
    private static final BigDecimal UCI_QUALITY_WEIGHT = new BigDecimal("0.3");
    private static final BigDecimal UCI_ENGAGEMENT_WEIGHT = new BigDecimal("0.3");
    private static final BigDecimal UCI_MAX_SCORE = new BigDecimal("100.0");

    // CPS Algorithm Constants
    private static final BigDecimal CPS_DIFFICULTY_WEIGHT = new BigDecimal("0.3");
    private static final BigDecimal CPS_USER_LEVEL_WEIGHT = new BigDecimal("0.25");
    private static final BigDecimal CPS_SUCCESS_RATE_WEIGHT = new BigDecimal("0.25");
    private static final BigDecimal CPS_ENGAGEMENT_WEIGHT = new BigDecimal("0.2");

    /**
     * Calculate User Commitment Index (UCI) score
     */
    public BigDecimal calculateUCI(User user) {
        try {
            UserStatistics stats = getUserStatistics(user);
            
            // Consistency Score (40%): Based on streaks and regular activity
            BigDecimal consistencyScore = calculateConsistencyScore(stats);
            
            // Quality Score (30%): Based on success rates and validations
            BigDecimal qualityScore = calculateQualityScore(stats);
            
            // Engagement Score (30%): Based on activity levels and social participation
            BigDecimal engagementScore = calculateEngagementScore(stats);
            
            // Calculate weighted UCI score
            BigDecimal uciScore = consistencyScore.multiply(UCI_CONSISTENCY_WEIGHT)
                    .add(qualityScore.multiply(UCI_QUALITY_WEIGHT))
                    .add(engagementScore.multiply(UCI_ENGAGEMENT_WEIGHT));
            
            // Ensure score doesn't exceed maximum
            if (uciScore.compareTo(UCI_MAX_SCORE) > 0) {
                uciScore = UCI_MAX_SCORE;
            }
            
            // Update user statistics
            stats.setUciScore(uciScore);
            userStatisticsRepository.save(stats);
            
            logger.debug("UCI calculated for user {}: {}", user.getId(), uciScore);
            return uciScore;
            
        } catch (Exception e) {
            logger.error("Error calculating UCI for user {}: {}", user.getId(), e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Calculate Challenge Priority Score (CPS) for personalized recommendations
     */
    public BigDecimal calculateCPS(User user, Challenge challenge) {
        try {
            UserStatistics stats = getUserStatistics(user);
            
            // Difficulty matching (30%): How well challenge difficulty matches user level
            BigDecimal difficultyScore = calculateDifficultyScore(stats, challenge);
            
            // User level alignment (25%): Challenge appropriateness for user level
            BigDecimal levelScore = calculateLevelScore(stats, challenge);
            
            // Success rate prediction (25%): Likelihood of user success
            BigDecimal successScore = calculateSuccessScore(stats, challenge);
            
            // Engagement potential (20%): How engaging this challenge type is for user
            BigDecimal engagementScore = calculateChallengeEngagementScore(stats, challenge);
            
            // Calculate weighted CPS score
            BigDecimal cpsScore = difficultyScore.multiply(CPS_DIFFICULTY_WEIGHT)
                    .add(levelScore.multiply(CPS_USER_LEVEL_WEIGHT))
                    .add(successScore.multiply(CPS_SUCCESS_RATE_WEIGHT))
                    .add(engagementScore.multiply(CPS_ENGAGEMENT_WEIGHT));
            
            logger.debug("CPS calculated for user {} and challenge {}: {}", 
                        user.getId(), challenge.getId(), cpsScore);
            return cpsScore;
            
        } catch (Exception e) {
            logger.error("Error calculating CPS for user {} and challenge {}: {}", 
                        user.getId(), challenge.getId(), e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Award achievement to user
     */
    public UserAchievement awardAchievement(User user, Achievement achievement, String context) {
        try {
            // Check if user already has this achievement
            Optional<UserAchievement> existing = userAchievementRepository.findByUserAndAchievement(user, achievement);
            
            if (existing.isPresent()) {
                // Increment count for repeatable achievements
                if (achievement.getIsRepeatable()) {
                    UserAchievement userAchievement = existing.get();
                    userAchievement.incrementCount();
                    userAchievement.setContext(context);
                    return userAchievementRepository.save(userAchievement);
                } else {
                    logger.debug("User {} already has non-repeatable achievement {}", 
                               user.getId(), achievement.getCode());
                    return existing.get();
                }
            }
            
            // Create new user achievement
            UserAchievement userAchievement = new UserAchievement(user, achievement, context);
            userAchievement = userAchievementRepository.save(userAchievement);
            
            // Update user statistics
            updateUserStatisticsForAchievement(user, achievement);
            
            logger.info("Achievement {} awarded to user {}", achievement.getCode(), user.getId());
            return userAchievement;
            
        } catch (Exception e) {
            logger.error("Error awarding achievement {} to user {}: {}", 
                        achievement.getCode(), user.getId(), e.getMessage());
            throw new RuntimeException("Failed to award achievement", e);
        }
    }

    /**
     * Check and award eligible achievements for user action
     */
    public List<UserAchievement> checkAndAwardAchievements(User user, String actionType, Integer value) {
        List<UserAchievement> newAchievements = new ArrayList<>();
        
        try {
            List<Achievement> eligibleAchievements = achievementRepository
                    .findEligibleAchievements(actionType, value);
            
            for (Achievement achievement : eligibleAchievements) {
                if (achievement.canBeEarnedBy(user)) {
                    UserAchievement awarded = awardAchievement(user, achievement, 
                            "Automated award for " + actionType + " = " + value);
                    newAchievements.add(awarded);
                }
            }
            
            logger.debug("Checked achievements for user {} action {}: {} new achievements", 
                        user.getId(), actionType, newAchievements.size());
            
        } catch (Exception e) {
            logger.error("Error checking achievements for user {} action {}: {}", 
                        user.getId(), actionType, e.getMessage());
        }
        
        return newAchievements;
    }

    /**
     * Get user progress summary
     */
    public UserProgressResponse getUserProgress(User user) {
        try {
            UserStatistics stats = getUserStatistics(user);
            List<UserAchievement> achievements = userAchievementRepository.findByUser(user);
            Long totalPoints = userAchievementRepository.getTotalPointsByUser(user);
            
            return new UserProgressResponse(
                stats.getCurrentLevel(),
                stats.getLevelPoints(),
                getPointsToNextLevel(stats),
                stats.getUciScore(),
                achievements.size(),
                totalPoints != null ? totalPoints : 0L,
                stats.getCurrentStreak(),
                stats.getLongestStreak(),
                stats.getSuccessRate()
            );
            
        } catch (Exception e) {
            logger.error("Error getting user progress for user {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Failed to get user progress", e);
        }
    }

    /**
     * Get leaderboard
     */
    public LeaderboardResponse getLeaderboard(int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<Object[]> pointsLeaderboard = userAchievementRepository.getPointsLeaderboard(pageable);
            List<Object[]> achievementLeaderboard = userAchievementRepository.getLeaderboard(pageable);
            
            return new LeaderboardResponse(
                convertToLeaderboardEntries(pointsLeaderboard, "points"),
                convertToLeaderboardEntries(achievementLeaderboard, "achievements")
            );
            
        } catch (Exception e) {
            logger.error("Error getting leaderboard: {}", e.getMessage());
            throw new RuntimeException("Failed to get leaderboard", e);
        }
    }

    /**
     * Get user achievements by category
     */
    public List<AchievementResponse> getUserAchievementsByCategory(User user, AchievementCategory category) {
        try {
            List<UserAchievement> userAchievements = userAchievementRepository
                    .findByUserAndCategory(user, category);
            
            return userAchievements.stream()
                    .map(this::convertToAchievementResponse)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("Error getting user achievements by category for user {}: {}", 
                        user.getId(), e.getMessage());
            throw new RuntimeException("Failed to get user achievements", e);
        }
    }

    // Private Helper Methods

    private UserStatistics getUserStatistics(User user) {
        return userStatisticsRepository.findByUser(user)
                .orElseGet(() -> createInitialUserStatistics(user));
    }

    private UserStatistics createInitialUserStatistics(User user) {
        UserStatistics stats = new UserStatistics();
        stats.setUser(user);
        return userStatisticsRepository.save(stats);
    }

    private BigDecimal calculateConsistencyScore(UserStatistics stats) {
        // Base score from current streak (max 50 points)
        BigDecimal streakScore = new BigDecimal(Math.min(stats.getCurrentStreak() * 5, 50));
        
        // Bonus for longest streak (max 30 points)
        BigDecimal longestStreakBonus = new BigDecimal(Math.min(stats.getLongestStreak() * 2, 30));
        
        // Activity consistency (max 20 points)
        BigDecimal activityScore = calculateActivityConsistency(stats);
        
        return streakScore.add(longestStreakBonus).add(activityScore);
    }

    private BigDecimal calculateQualityScore(UserStatistics stats) {
        // Success rate (max 60 points)
        BigDecimal successScore = stats.getSuccessRate().multiply(new BigDecimal("0.6"));
        
        // Validation rate (max 40 points)
        BigDecimal validationScore = calculateValidationQuality(stats);
        
        return successScore.add(validationScore);
    }

    private BigDecimal calculateEngagementScore(UserStatistics stats) {
        // Direct engagement rate (max 70 points)
        BigDecimal engagementScore = stats.getEngagementRate().multiply(new BigDecimal("0.7"));
        
        // Social score (max 30 points)
        BigDecimal socialScore = stats.getSocialScore().multiply(new BigDecimal("0.3"));
        
        return engagementScore.add(socialScore);
    }

    private BigDecimal calculateActivityConsistency(UserStatistics stats) {
        // Simplified calculation - in production, this would analyze activity patterns
        return new BigDecimal("15.0"); // Placeholder
    }

    private BigDecimal calculateValidationQuality(UserStatistics stats) {
        if (stats.getTotalValidationsReceived() == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal validationRate = new BigDecimal(stats.getPositiveValidationsReceived())
                .divide(new BigDecimal(stats.getTotalValidationsReceived()), 2, RoundingMode.HALF_UP);
        
        return validationRate.multiply(new BigDecimal("40"));
    }

    private BigDecimal calculateDifficultyScore(UserStatistics stats, Challenge challenge) {
        // Simplified difficulty matching - would use challenge difficulty in production
        return new BigDecimal("75.0"); // Placeholder
    }

    private BigDecimal calculateLevelScore(UserStatistics stats, Challenge challenge) {
        // Level appropriateness calculation
        return new BigDecimal("80.0"); // Placeholder
    }

    private BigDecimal calculateSuccessScore(UserStatistics stats, Challenge challenge) {
        // Success probability based on user's success rate and challenge type
        return stats.getSuccessRate().multiply(new BigDecimal("0.8"));
    }

    private BigDecimal calculateChallengeEngagementScore(UserStatistics stats, Challenge challenge) {
        // Engagement prediction based on user preferences and challenge type
        return stats.getEngagementRate().multiply(new BigDecimal("0.9"));
    }

    private void updateUserStatisticsForAchievement(User user, Achievement achievement) {
        UserStatistics stats = getUserStatistics(user);
        stats.setTotalAchievements(stats.getTotalAchievements() + 1);
        stats.setTotalPoints(stats.getTotalPoints() + achievement.getPoints());
        
        // Check for level up
        checkAndUpdateLevel(stats);
        
        userStatisticsRepository.save(stats);
    }

    private void checkAndUpdateLevel(UserStatistics stats) {
        int newLevel = calculateLevelFromPoints(stats.getTotalPoints());
        if (newLevel > stats.getCurrentLevel()) {
            stats.setCurrentLevel(newLevel);
            stats.setLevelPoints(stats.getTotalPoints() % 1000); // Points in current level
        }
    }

    private int calculateLevelFromPoints(Long totalPoints) {
        return (int) (totalPoints / 1000) + 1; // 1000 points per level
    }

    private int getPointsToNextLevel(UserStatistics stats) {
        return 1000 - stats.getLevelPoints();
    }

    private List<Object> convertToLeaderboardEntries(List<Object[]> data, String type) {
        return data.stream()
                .map(row -> new Object() {
                    public final User user = (User) row[0];
                    public final Number value = (Number) row[1];
                    public final String valueType = type;
                })
                .collect(Collectors.toList());
    }

    private AchievementResponse convertToAchievementResponse(UserAchievement userAchievement) {
        Achievement achievement = userAchievement.getAchievement();
        return new AchievementResponse(
            achievement.getId(),
            achievement.getCode(),
            achievement.getTitle(),
            achievement.getDescription(),
            achievement.getCategory(),
            achievement.getLevel(),
            achievement.getPoints(),
            userAchievement.getEarnedAt(),
            userAchievement.getEarnedCount()
        );
    }
}
