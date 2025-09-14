package com.impulse.domain.model;

import com.impulse.domain.enums.ValidationPriority;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "validator_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidatorAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validator_id", nullable = false)
    private Validator validator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_id", nullable = false)
    private Evidence evidence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidationPriority priority;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "validation_result", columnDefinition = "TEXT")
    private String validationResult;

    @Column(name = "validator_notes", columnDefinition = "TEXT")
    private String validatorNotes;

    @Column(name = "confidence_level")
    @Min(0)
    @Max(100)
    private Integer confidenceLevel;

    @Column(name = "time_spent_minutes")
    private Integer timeSpentMinutes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        assignedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
