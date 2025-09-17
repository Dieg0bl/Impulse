package com.impulse.adapters.persistence.coach.entity;

import com.impulse.domain.enums.CoachStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coaches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoachJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "specializations", columnDefinition = "TEXT")
    private String specializations;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "certification_url", length = 500)
    private String certificationUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private CoachStatus status;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "total_ratings")
    private Integer totalRatings;

    @Column(name = "total_sessions")
    private Integer totalSessions;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isVerified == null) {
            isVerified = false;
        }
        if (totalRatings == null) {
            totalRatings = 0;
        }
        if (totalSessions == null) {
            totalSessions = 0;
        }
        if (averageRating == null) {
            averageRating = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

