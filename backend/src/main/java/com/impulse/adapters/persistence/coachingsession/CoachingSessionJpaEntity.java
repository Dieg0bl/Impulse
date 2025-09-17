package com.impulse.adapters.persistence.coachingsession;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "coaching_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoachingSessionJpaEntity {
    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "student_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String studentId;

    @Column(name = "coach_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String coachId;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(precision = 3, scale = 2)
    private Double rating;

    @Column(nullable = false)
    private Boolean completed;

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