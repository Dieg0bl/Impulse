package com.impulse.economy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "economic_guardrails")
public class EconomicGuardrails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double monthlyRewardsPool;
    private double maxRewardsPercentage;
    private double maxBenefitPercentage;
    private double userMonthlyLimit;
    private double userDailyThreshold;
    private int userImpulseProtection;
    // getters y setters
}
