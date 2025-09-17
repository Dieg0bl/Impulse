package com.impulse.validation;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "validator_profile")
public class ValidatorProfile {
    @Id
    private String id;
    private String userId;
    private String name;
    private Double averageResponseHours;
    private Integer optimalValidations;
    private Integer standardValidations;
    private Integer delayedValidations;
    private Integer timeoutValidations;
    private Integer totalValidations;
    private Double slaScore;
    private Integer currentStreak;
    private LocalDateTime lastActivityDate;
    private Boolean isActive;
    private Integer maxDailyValidations;
    private Integer currentDailyValidations;
    private String timezone;
    private Boolean autoAssignEnabled;
    private Double reputationScore;
    private String specializations;
    private String languagePreferences;
    // getters y setters
}
