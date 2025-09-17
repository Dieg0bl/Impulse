package com.impulse.coaching;

import jakarta.persistence.*;

@Entity
@Table(name = "coaching_tier")
public class CoachingTier {
    @Id
    private String tier;
    private String name;
    private String description;
    private double monthlyPrice;
    private int responseTimeHours;
    private int monthlyInteractions;
    private boolean includesVideoCalls;
    private boolean personalizedPlan;
    private boolean prioritySupport;
    // getters y setters
}
