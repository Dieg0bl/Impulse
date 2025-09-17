package com.impulse.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coaching_session")
public class CoachingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String coachId;
    private String sessionType;
    private LocalDateTime scheduledDate;
    private Integer durationMinutes;
    private String notes;
    private Boolean completed;
    // getters y setters
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return this.id;
    }
}
