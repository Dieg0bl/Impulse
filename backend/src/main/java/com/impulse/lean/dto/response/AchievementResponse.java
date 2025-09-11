package com.impulse.lean.dto.response;

import java.time.LocalDateTime;

import com.impulse.lean.domain.model.AchievementCategory;
import com.impulse.lean.domain.model.AchievementLevel;

/**
 * IMPULSE LEAN v1 - Achievement Response DTO
 * 
 * Response DTO for achievement data
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public class AchievementResponse {

    private Long id;
    private String code;
    private String title;
    private String description;
    private AchievementCategory category;
    private AchievementLevel level;
    private Integer points;
    private LocalDateTime earnedAt;
    private Integer earnedCount;

    // Constructors
    public AchievementResponse() {}

    public AchievementResponse(Long id, String code, String title, String description,
                              AchievementCategory category, AchievementLevel level, Integer points,
                              LocalDateTime earnedAt, Integer earnedCount) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.description = description;
        this.category = category;
        this.level = level;
        this.points = points;
        this.earnedAt = earnedAt;
        this.earnedCount = earnedCount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AchievementCategory getCategory() {
        return category;
    }

    public void setCategory(AchievementCategory category) {
        this.category = category;
    }

    public AchievementLevel getLevel() {
        return level;
    }

    public void setLevel(AchievementLevel level) {
        this.level = level;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(LocalDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }

    public Integer getEarnedCount() {
        return earnedCount;
    }

    public void setEarnedCount(Integer earnedCount) {
        this.earnedCount = earnedCount;
    }
}
