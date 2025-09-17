package com.impulse.application.coaching.dto;

public class CoachingTierResponse {
    private final String tier;
    private final String name;
    private final String description;
    private final double monthlyPrice;
    private final int responseTimeHours;
    private final int monthlyInteractions;
    private final boolean includesVideoCalls;
    private final boolean personalizedPlan;
    private final boolean prioritySupport;

    public CoachingTierResponse(String tier, String name, String description, double monthlyPrice,
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

    public String getTier() { return tier; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getMonthlyPrice() { return monthlyPrice; }
    public int getResponseTimeHours() { return responseTimeHours; }
    public int getMonthlyInteractions() { return monthlyInteractions; }
    public boolean includesVideoCalls() { return includesVideoCalls; }
    public boolean hasPersonalizedPlan() { return personalizedPlan; }
    public boolean hasPrioritySupport() { return prioritySupport; }
}
