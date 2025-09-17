package com.impulse.validation;

import jakarta.persistence.*;

@Entity
@Table(name = "redistribution_rule")
public class RedistributionRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String triggerType;
    private Integer timeoutHours;
    private String redistributionStrategy;
    private Boolean compensationRequired;
    private Double compensationAmount;
    private String notificationTemplate;
    // getters y setters
}
