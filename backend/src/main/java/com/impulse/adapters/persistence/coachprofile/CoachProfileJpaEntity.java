package com.impulse.adapters.persistence.coachprofile;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "coach_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoachProfileJpaEntity {
    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(nullable = false)
    private String name;

    private String photo;

    @Column(columnDefinition = "TEXT")
    private String specializations;

    private String languages;

    private String timezone;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "certifications_count")
    private Integer certificationsCount;

    @Column(name = "total_students")
    private Integer totalStudents;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private Double averageRating;

    @Column(name = "success_rate", precision = 5, scale = 4)
    private Double successRate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

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