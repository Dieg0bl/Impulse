package com.impulse.domain.model;

import com.impulse.domain.enums.ValidationResult;
import com.impulse.domain.enums.ValidationType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "validations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Validation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidationResult result;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "validator_notes", columnDefinition = "TEXT")
    private String validatorNotes;

    @Column(name = "validation_score")
    @Min(0)
    @Max(100)
    private Integer validationScore;

    @Column(name = "validation_date")
    private LocalDateTime validationDate;

    @Column(name = "validation_deadline")
    private LocalDateTime validationDeadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidationType type;

    @Column(name = "is_automated")
    private Boolean isAutomated = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_id", nullable = false)
    private Evidence evidence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validator_id")
    private User validator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

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
