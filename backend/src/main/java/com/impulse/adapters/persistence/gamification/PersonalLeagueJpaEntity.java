package com.impulse.adapters.persistence.gamification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "personal_leagues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalLeagueJpaEntity {
    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tier;

    @Column(name = "min_validation_rate", nullable = false)
    private double minValidationRate;

    @Column(name = "min_cred_points", nullable = false)
    private int minCredPoints;

    @Column(name = "min_streak_days", nullable = false)
    private int minStreakDays;

    @Column(name = "window_days", nullable = false)
    private int windowDays;

    @Column(name = "badge_icon")
    private String badgeIcon;

    @Column(name = "frame_color")
    private String frameColor;

    @Column(name = "cred_bonus", nullable = false)
    private double credBonus;

    @Column(name = "prestige_points", nullable = false)
    private int prestigePoints;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
