package com.impulse.gamification;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "streak_freeze")
public class StreakFreeze {
    public String getUserId() { return userId; }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private LocalDateTime freezeDate;
    private String reason;
    private int duration;
    private boolean isActive;
    // getters y setters
}
