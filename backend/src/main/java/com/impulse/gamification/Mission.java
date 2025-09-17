package com.impulse.gamification;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mission")
public class Mission {
    @Id
    private String id;
    private String type;
    private String category;
    private String name;
    private String description;
    private boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String difficulty;
    // getters y setters
}
