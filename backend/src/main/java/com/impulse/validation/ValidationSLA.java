package com.impulse.validation;

import jakarta.persistence.*;

@Entity
@Table(name = "validation_sla")
public class ValidationSLA {
    @Id
    private String level;
    private int timeoutHours;
    private double slaReward;
    private String description;
    private String userMessage;
    private String coachMessage;
    // getters y setters
}
