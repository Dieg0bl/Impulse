package com.impulse.gamification;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_streak")
public class UserStreak {
    public String getUserId() { return userId; }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private int currentStreak;
    private int longestStreak;
    private LocalDateTime lastActivityDate;
    private int freezesUsed;
    private int freezesAvailable;
    private String streakType;
    private boolean isActive;
    // getters y setters
}
