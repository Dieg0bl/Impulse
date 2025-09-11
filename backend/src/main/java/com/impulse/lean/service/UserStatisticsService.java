package com.impulse.lean.service;

import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.model.UserStatistics;
import com.impulse.lean.repository.UserStatisticsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * IMPULSE LEAN v1 - User Statistics Service
 * 
 * Service for user statistics management and calculations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Service
@Transactional
public class UserStatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(UserStatisticsService.class);

    @Autowired
    private UserStatisticsRepository userStatisticsRepository;

    /**
     * Get or create user statistics
     */
    public UserStatistics getOrCreateUserStatistics(User user) {
        Optional<UserStatistics> existing = userStatisticsRepository.findByUser(user);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Create new statistics
        UserStatistics stats = new UserStatistics();
        stats.setUser(user);
        stats.setCreatedAt(LocalDateTime.now());
        
        return userStatisticsRepository.save(stats);
    }

    /**
     * Update challenge statistics
     */
    public void updateChallengeStatistics(User user, boolean completed, boolean won) {
        try {
            UserStatistics stats = getOrCreateUserStatistics(user);
            
            stats.setChallengesAttempted(stats.getChallengesAttempted() + 1);
            if (completed) {
                stats.setChallengesCompleted(stats.getChallengesCompleted() + 1);
            }
            if (won) {
                stats.setChallengesWon(stats.getChallengesWon() + 1);
            }
            
            // Update success rate
            stats.setSuccessRate(stats.calculateSuccessRate());
            
            // Update last activity
            stats.setLastActivityDate(LocalDateTime.now());
            
            userStatisticsRepository.save(stats);
            logger.debug("Updated challenge statistics for user {}", user.getId());
            
        } catch (Exception e) {
            logger.error("Error updating challenge statistics for user {}: {}", user.getId(), e.getMessage());
        }
    }

    /**
     * Update evidence statistics
     */
    public void updateEvidenceStatistics(User user, boolean approved) {
        try {
            UserStatistics stats = getOrCreateUserStatistics(user);
            
            stats.setEvidenceSubmitted(stats.getEvidenceSubmitted() + 1);
            if (approved) {
                stats.setEvidenceApproved(stats.getEvidenceApproved() + 1);
            } else {
                stats.setEvidenceRejected(stats.getEvidenceRejected() + 1);
            }
            
            // Update last activity
            stats.setLastActivityDate(LocalDateTime.now());
            
            userStatisticsRepository.save(stats);
            logger.debug("Updated evidence statistics for user {}", user.getId());
            
        } catch (Exception e) {
            logger.error("Error updating evidence statistics for user {}: {}", user.getId(), e.getMessage());
        }
    }

    /**
     * Update validation statistics
     */
    public void updateValidationStatistics(User user, boolean positive) {
        try {
            UserStatistics stats = getOrCreateUserStatistics(user);
            
            stats.setValidationsPerformed(stats.getValidationsPerformed() + 1);
            if (positive) {
                stats.setPositiveValidationsReceived(stats.getPositiveValidationsReceived() + 1);
            } else {
                stats.setNegativeValidationsReceived(stats.getNegativeValidationsReceived() + 1);
            }
            
            // Update last activity
            stats.setLastActivityDate(LocalDateTime.now());
            
            userStatisticsRepository.save(stats);
            logger.debug("Updated validation statistics for user {}", user.getId());
            
        } catch (Exception e) {
            logger.error("Error updating validation statistics for user {}: {}", user.getId(), e.getMessage());
        }
    }

    /**
     * Update social interaction statistics
     */
    public void updateSocialStatistics(User user, String interactionType) {
        try {
            UserStatistics stats = getOrCreateUserStatistics(user);
            
            switch (interactionType.toLowerCase()) {
                case "like_given":
                    stats.setLikesGiven(stats.getLikesGiven() + 1);
                    break;
                case "like_received":
                    stats.setLikesReceived(stats.getLikesReceived() + 1);
                    break;
                case "comment":
                    stats.setCommentsCount(stats.getCommentsCount() + 1);
                    break;
                case "share":
                    stats.setSharesCount(stats.getSharesCount() + 1);
                    break;
                case "follow":
                    stats.setFollowersCount(stats.getFollowersCount() + 1);
                    break;
                case "following":
                    stats.setFollowingCount(stats.getFollowingCount() + 1);
                    break;
            }
            
            // Update last activity
            stats.setLastActivityDate(LocalDateTime.now());
            
            userStatisticsRepository.save(stats);
            logger.debug("Updated social statistics for user {} - {}", user.getId(), interactionType);
            
        } catch (Exception e) {
            logger.error("Error updating social statistics for user {}: {}", user.getId(), e.getMessage());
        }
    }

    /**
     * Update streak information
     */
    public void updateStreak(User user, boolean activityToday) {
        try {
            UserStatistics stats = getOrCreateUserStatistics(user);
            
            if (activityToday) {
                stats.setCurrentStreak(stats.getCurrentStreak() + 1);
                if (stats.getCurrentStreak() > stats.getLongestStreak()) {
                    stats.setLongestStreak(stats.getCurrentStreak());
                }
            } else {
                stats.setCurrentStreak(0);
            }
            
            stats.setLastActivityDate(LocalDateTime.now());
            userStatisticsRepository.save(stats);
            
            logger.debug("Updated streak for user {}: current={}, longest={}", 
                        user.getId(), stats.getCurrentStreak(), stats.getLongestStreak());
            
        } catch (Exception e) {
            logger.error("Error updating streak for user {}: {}", user.getId(), e.getMessage());
        }
    }

    /**
     * Add points to user
     */
    public void addPoints(User user, Integer points) {
        try {
            UserStatistics stats = getOrCreateUserStatistics(user);
            
            stats.setTotalPoints(stats.getTotalPoints() + points);
            stats.setWeeklyPoints(stats.getWeeklyPoints() + points);
            stats.setMonthlyPoints(stats.getMonthlyPoints() + points);
            
            // Check for level up
            checkAndUpdateLevel(stats);
            
            userStatisticsRepository.save(stats);
            logger.debug("Added {} points to user {}. Total: {}", points, user.getId(), stats.getTotalPoints());
            
        } catch (Exception e) {
            logger.error("Error adding points for user {}: {}", user.getId(), e.getMessage());
        }
    }

    /**
     * Get top users by UCI score
     */
    public List<UserStatistics> getTopUsersByUCI(int limit) {
        Page<UserStatistics> page = userStatisticsRepository.findAllOrderByUciScoreDesc(
                Pageable.ofSize(limit));
        return page.getContent();
    }

    /**
     * Get top users by total points
     */
    public List<UserStatistics> getTopUsersByPoints(int limit) {
        Page<UserStatistics> page = userStatisticsRepository.findAllOrderByTotalPointsDesc(
                Pageable.ofSize(limit));
        return page.getContent();
    }

    /**
     * Get users by minimum level
     */
    public List<UserStatistics> getUsersByMinLevel(Integer minLevel) {
        return userStatisticsRepository.findByMinLevel(minLevel);
    }

    /**
     * Get active users since date
     */
    public List<UserStatistics> getActiveUsersSince(LocalDateTime since) {
        return userStatisticsRepository.findActiveUsersSince(since);
    }

    /**
     * Get inactive users before date
     */
    public List<UserStatistics> getInactiveUsersBefore(LocalDateTime before) {
        return userStatisticsRepository.findInactiveUsersBefore(before);
    }

    /**
     * Get system statistics
     */
    public Object getSystemStatistics() {
        try {
            BigDecimal avgUciScore = userStatisticsRepository.getAverageUciScore();
            Double avgTotalPoints = userStatisticsRepository.getAverageTotalPoints();
            BigDecimal avgSuccessRate = userStatisticsRepository.getAverageSuccessRate();
            BigDecimal avgEngagementRate = userStatisticsRepository.getAverageEngagementRate();
            Integer maxStreak = userStatisticsRepository.getMaxStreak();
            
            return new Object() {
                public final BigDecimal averageUciScore = avgUciScore != null ? avgUciScore : BigDecimal.ZERO;
                public final Double averageTotalPoints = avgTotalPoints != null ? avgTotalPoints : 0.0;
                public final BigDecimal averageSuccessRate = avgSuccessRate != null ? avgSuccessRate : BigDecimal.ZERO;
                public final BigDecimal averageEngagementRate = avgEngagementRate != null ? avgEngagementRate : BigDecimal.ZERO;
                public final Integer maxStreak = maxStreak != null ? maxStreak : 0;
            };
            
        } catch (Exception e) {
            logger.error("Error getting system statistics: {}", e.getMessage());
            return new Object() {
                public final String error = "Failed to retrieve system statistics";
            };
        }
    }

    /**
     * Get level distribution
     */
    public List<Object[]> getLevelDistribution() {
        return userStatisticsRepository.getLevelDistribution();
    }

    /**
     * Reset weekly points for all users
     */
    public void resetWeeklyPoints() {
        try {
            List<UserStatistics> allStats = userStatisticsRepository.findAll();
            for (UserStatistics stats : allStats) {
                stats.setWeeklyPoints(0);
            }
            userStatisticsRepository.saveAll(allStats);
            logger.info("Reset weekly points for {} users", allStats.size());
            
        } catch (Exception e) {
            logger.error("Error resetting weekly points: {}", e.getMessage());
        }
    }

    /**
     * Reset monthly points for all users
     */
    public void resetMonthlyPoints() {
        try {
            List<UserStatistics> allStats = userStatisticsRepository.findAll();
            for (UserStatistics stats : allStats) {
                stats.setMonthlyPoints(0);
            }
            userStatisticsRepository.saveAll(allStats);
            logger.info("Reset monthly points for {} users", allStats.size());
            
        } catch (Exception e) {
            logger.error("Error resetting monthly points: {}", e.getMessage());
        }
    }

    // Private helper methods

    private void checkAndUpdateLevel(UserStatistics stats) {
        int newLevel = calculateLevelFromPoints(stats.getTotalPoints());
        if (newLevel > stats.getCurrentLevel()) {
            stats.setCurrentLevel(newLevel);
            logger.info("User {} leveled up to level {}", 
                       stats.getUser().getId(), newLevel);
        }
    }

    private int calculateLevelFromPoints(Integer totalPoints) {
        // Simple level calculation: 1000 points per level
        return (totalPoints / 1000) + 1;
    }
}
