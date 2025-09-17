package com.impulse.adapters.persistence.coaching;

import jakarta.persistence.*;

@Entity
@Table(name = "coaching_tier")
public class CoachingTierEntity {
    @Id
    private String tier;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "monthly_price")
    private double monthlyPrice;

    @Column(name = "response_time_hours")
    private int responseTimeHours;

    @Column(name = "monthly_interactions")
    private int monthlyInteractions;

    @Column(name = "includes_video_calls")
    private boolean includesVideoCalls;

    @Column(name = "personalized_plan")
    private boolean personalizedPlan;

    @Column(name = "priority_support")
    private boolean prioritySupport;

    public CoachingTierEntity() {}

    public CoachingTierEntity(String tier, String name, String description, double monthlyPrice,
                             int responseTimeHours, int monthlyInteractions, boolean includesVideoCalls,
                             boolean personalizedPlan, boolean prioritySupport) {
        this.tier = tier;
        this.name = name;
        this.description = description;
        this.monthlyPrice = monthlyPrice;
        this.responseTimeHours = responseTimeHours;
        this.monthlyInteractions = monthlyInteractions;
        this.includesVideoCalls = includesVideoCalls;
        this.personalizedPlan = personalizedPlan;
        this.prioritySupport = prioritySupport;
    }

    // Getters and setters
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(double monthlyPrice) { this.monthlyPrice = monthlyPrice; }

    public int getResponseTimeHours() { return responseTimeHours; }
    public void setResponseTimeHours(int responseTimeHours) { this.responseTimeHours = responseTimeHours; }

    public int getMonthlyInteractions() { return monthlyInteractions; }
    public void setMonthlyInteractions(int monthlyInteractions) { this.monthlyInteractions = monthlyInteractions; }

    public boolean isIncludesVideoCalls() { return includesVideoCalls; }
    public void setIncludesVideoCalls(boolean includesVideoCalls) { this.includesVideoCalls = includesVideoCalls; }

    public boolean isPersonalizedPlan() { return personalizedPlan; }
    public void setPersonalizedPlan(boolean personalizedPlan) { this.personalizedPlan = personalizedPlan; }

    public boolean isPrioritySupport() { return prioritySupport; }
    public void setPrioritySupport(boolean prioritySupport) { this.prioritySupport = prioritySupport; }
}
