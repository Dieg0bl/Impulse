package com.impulse.adapters.persistence.validatorassignment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "validator_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidatorAssignmentJpaEntity {

    @Id
    private UUID id;

    @Column(name = "validator_id", nullable = false)
    private UUID validatorId;

    @Column(name = "evidence_id", nullable = false)
    private UUID evidenceId;

    @Column(name = "assigned_date", nullable = false)
    private LocalDateTime assignedDate;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "priority", length = 20)
    private String priority;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "assignment_metadata", columnDefinition = "JSON")
    private String assignmentMetadata;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (assignedDate == null) {
            assignedDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
