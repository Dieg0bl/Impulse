package com.impulse.lean.controller;

import com.impulse.lean.domain.model.AchievementCategory;
import com.impulse.lean.domain.model.User;
import com.impulse.lean.dto.response.AchievementResponse;
import com.impulse.lean.dto.response.LeaderboardResponse;
import com.impulse.lean.dto.response.UserProgressResponse;
import com.impulse.lean.service.GamificationService;
// import com.impulse.lean.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * IMPULSE LEAN v1 - Gamification Controller
 * 
 * REST API endpoints for gamification features including achievements,
 * user progress, leaderboards, and UCI/CPS scores
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/gamification")
@Tag(name = "Gamification", description = "Gamification and achievement system")
public class GamificationController {

    private static final Logger logger = LoggerFactory.getLogger(GamificationController.class);

    @Autowired
    private GamificationService gamificationService;

    // @Autowired
    // private UserService userService;

    /**
     * Get user progress summary
     */
    @GetMapping("/progress/{userId}")
    @Operation(summary = "Get user progress", 
               description = "Get complete progress summary for a user including level, points, achievements")
    public ResponseEntity<UserProgressResponse> getUserProgress(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        try {
            User user = userService.findById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            UserProgressResponse progress = gamificationService.getUserProgress(user);
            return ResponseEntity.ok(progress);

        } catch (Exception e) {
            logger.error("Error getting user progress for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Calculate UCI score for user
     */
    @GetMapping("/uci/{userId}")
    @Operation(summary = "Calculate UCI score", 
               description = "Calculate User Commitment Index (UCI) score for a user")
    public ResponseEntity<BigDecimal> calculateUCI(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        try {
            User user = userService.findById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            BigDecimal uciScore = gamificationService.calculateUCI(user);
            return ResponseEntity.ok(uciScore);

        } catch (Exception e) {
            logger.error("Error calculating UCI for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get user achievements by category
     */
    @GetMapping("/achievements/{userId}")
    @Operation(summary = "Get user achievements", 
               description = "Get all achievements earned by a user, optionally filtered by category")
    public ResponseEntity<List<AchievementResponse>> getUserAchievements(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Achievement category filter") @RequestParam(required = false) AchievementCategory category) {
        
        try {
            User user = userService.findById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            List<AchievementResponse> achievements;
            if (category != null) {
                achievements = gamificationService.getUserAchievementsByCategory(user, category);
            } else {
                // If no category specified, get all achievements
                achievements = gamificationService.getUserAchievementsByCategory(user, null);
            }

            return ResponseEntity.ok(achievements);

        } catch (Exception e) {
            logger.error("Error getting achievements for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get leaderboard
     */
    @GetMapping("/leaderboard")
    @Operation(summary = "Get leaderboard", 
               description = "Get system-wide leaderboard for points and achievements")
    public ResponseEntity<LeaderboardResponse> getLeaderboard(
            @Parameter(description = "Number of top users to return") @RequestParam(defaultValue = "10") int limit) {
        
        try {
            if (limit <= 0 || limit > 100) {
                limit = 10; // Default to 10 if invalid
            }

            LeaderboardResponse leaderboard = gamificationService.getLeaderboard(limit);
            return ResponseEntity.ok(leaderboard);

        } catch (Exception e) {
            logger.error("Error getting leaderboard: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Manual achievement check and award
     */
    @PostMapping("/check-achievements/{userId}")
    @Operation(summary = "Check and award achievements", 
               description = "Manually trigger achievement check for a user based on action")
    public ResponseEntity<List<String>> checkAndAwardAchievements(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Action type") @RequestParam String actionType,
            @Parameter(description = "Action value") @RequestParam Integer value) {
        
        try {
            User user = userService.findById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            var newAchievements = gamificationService.checkAndAwardAchievements(user, actionType, value);
            
            List<String> achievementCodes = newAchievements.stream()
                    .map(ua -> ua.getAchievement().getCode())
                    .toList();

            return ResponseEntity.ok(achievementCodes);

        } catch (Exception e) {
            logger.error("Error checking achievements for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get available achievement categories
     */
    @GetMapping("/categories")
    @Operation(summary = "Get achievement categories", 
               description = "Get all available achievement categories")
    public ResponseEntity<AchievementCategory[]> getAchievementCategories() {
        try {
            return ResponseEntity.ok(AchievementCategory.values());
        } catch (Exception e) {
            logger.error("Error getting achievement categories: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get user statistics summary
     */
    @GetMapping("/stats/{userId}")
    @Operation(summary = "Get user statistics", 
               description = "Get detailed statistics for a user")
    public ResponseEntity<Object> getUserStatistics(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        try {
            User user = userService.findById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // This would return UserStatistics directly or a DTO
            // For now, return basic progress info
            UserProgressResponse progress = gamificationService.getUserProgress(user);
            return ResponseEntity.ok(progress);

        } catch (Exception e) {
            logger.error("Error getting statistics for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
