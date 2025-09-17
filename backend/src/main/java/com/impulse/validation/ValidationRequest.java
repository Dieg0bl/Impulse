package com.impulse.validation;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "validation_request")
public class ValidationRequest {
    @Id
    private String id;
    private String userId;
    private String challengeId;
    private String evidenceId;
    private LocalDateTime requestDate;
    private String assignedValidatorId;
    private String backupValidatorIds;
    private String currentSlaLevel;
    private Integer escalationLevel;
    private Boolean isRedistributed;
    private Double urgencyBoost;
    private String status;
    private LocalDateTime completedDate;
    private Double actualResponseHours;
    // getters y setters
}
