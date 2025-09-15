package com.impulse.domain.model;

import java.time.LocalDateTime;

import com.impulse.domain.enums.ValidatorSpecialty;
import com.impulse.domain.enums.ValidatorStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "validators")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Validator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidatorStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidatorSpecialty specialty;

    @Column(name = "experience_level")
    @Min(1)
    @Max(10)
    private Integer experienceLevel = 1;

    @Column(name = "validation_count")
    private Integer validationCount = 0;

    @Column(name = "accuracy_score")
    private Double accuracyScore = 0.0;

    @Column(name = "average_response_time_hours")
    private Integer averageResponseTimeHours;

    @Column(name = "is_certified")
    private Boolean isCertified = false;

    @Column(name = "certification_date")
    private LocalDateTime certificationDate;

    @Column(name = "certification_expires_at")
    private LocalDateTime certificationExpiresAt;

    @Column(name = "max_concurrent_validations")
    private Integer maxConcurrentValidations = 5;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
