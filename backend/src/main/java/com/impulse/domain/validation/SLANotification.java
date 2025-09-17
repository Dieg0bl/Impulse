package com.impulse.validation;

import jakarta.persistence.*;

@Entity
@Table(name = "sla_notification")
public class SLANotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String recipient;
    private Integer triggerHours;
    private String template;
    private String urgency;
    private Boolean includeCompensation;
    // getters y setters
}
