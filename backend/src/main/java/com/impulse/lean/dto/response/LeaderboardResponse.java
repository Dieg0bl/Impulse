package com.impulse.lean.dto.response;

import java.util.List;

/**
 * IMPULSE LEAN v1 - Leaderboard Response DTO
 * 
 * Response DTO for leaderboard data
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public class LeaderboardResponse {

    private List<Object> pointsLeaderboard;
    private List<Object> achievementLeaderboard;

    // Constructors
    public LeaderboardResponse() {}

    public LeaderboardResponse(List<Object> pointsLeaderboard, List<Object> achievementLeaderboard) {
        this.pointsLeaderboard = pointsLeaderboard;
        this.achievementLeaderboard = achievementLeaderboard;
    }

    // Getters and Setters
    public List<Object> getPointsLeaderboard() {
        return pointsLeaderboard;
    }

    public void setPointsLeaderboard(List<Object> pointsLeaderboard) {
        this.pointsLeaderboard = pointsLeaderboard;
    }

    public List<Object> getAchievementLeaderboard() {
        return achievementLeaderboard;
    }

    public void setAchievementLeaderboard(List<Object> achievementLeaderboard) {
        this.achievementLeaderboard = achievementLeaderboard;
    }
}
