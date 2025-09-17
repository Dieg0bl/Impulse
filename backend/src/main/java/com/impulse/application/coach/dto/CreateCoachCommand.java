package com.impulse.application.coach.dto;

import java.math.BigDecimal;

/**
 * CreateCoachCommand - Comando para crear un nuevo coach
 */
public class CreateCoachCommand {
    private final String userId;
    private final String bio;
    private final String specializations;
    private final Integer yearsExperience;
    private final BigDecimal hourlyRate;
    private final String certificationUrl;
    public CreateCoachCommand(String userId, String bio, String specializations, Integer yearsExperience, BigDecimal hourlyRate, String certificationUrl) {
        this.userId = userId;
        this.bio = bio;
        this.specializations = specializations;
        this.yearsExperience = yearsExperience;
        this.hourlyRate = hourlyRate;
        this.certificationUrl = certificationUrl;
    }
    public String getUserId() { return userId; }
    public String getBio() { return bio; }
    public String getSpecializations() { return specializations; }
    public Integer getYearsExperience() { return yearsExperience; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public String getCertificationUrl() { return certificationUrl; }
}
