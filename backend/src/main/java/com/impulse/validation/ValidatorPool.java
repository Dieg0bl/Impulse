package com.impulse.validation;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "validator_pool")
public class ValidatorPool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String region;
    private String timezone;
    private String availableValidators;
    private Double avgResponseTime;
    private Double capacityUtilization;
    private LocalDateTime lastUpdated;
    // getters y setters
}
