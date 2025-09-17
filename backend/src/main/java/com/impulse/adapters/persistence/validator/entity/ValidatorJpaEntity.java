package com.impulse.adapters.persistence.validator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "validators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidatorJpaEntity {

    @Id
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "specializations", columnDefinition = "TEXT")
    private String specializations;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "rating", precision = 3, scale = 2)
    private Double rating;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "certification_url", length = 500)
    private String certificationUrl;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "total_validations")
    private Integer totalValidations;

    @Column(name = "successful_validations")
    private Integer successfulValidations;

    @Column(name = "last_validation_at")
    private LocalDateTime lastValidationAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
        if (totalValidations == null) {
            totalValidations = 0;
        }
        if (successfulValidations == null) {
            successfulValidations = 0;
        }
        if (rating == null) {
            rating = 0.0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
