package com.impulse.domain.coaching;

public class CoachingTier {
    private final String tier;
    private final String name;
    private final String description;
    private final double monthlyPrice;
    private final int responseTimeHours;
    private final int monthlyInteractions;
    private final boolean includesVideoCalls;
    private final boolean personalizedPlan;
    private final boolean prioritySupport;

    private CoachingTier(Builder builder) {
        this.tier = builder.tier;
        this.name = builder.name;
        this.description = builder.description;
        this.monthlyPrice = builder.monthlyPrice;
        this.responseTimeHours = builder.responseTimeHours;
        this.monthlyInteractions = builder.monthlyInteractions;
        this.includesVideoCalls = builder.includesVideoCalls;
        this.personalizedPlan = builder.personalizedPlan;
        this.prioritySupport = builder.prioritySupport;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String tier;
        private String name;
        private String description;
        private double monthlyPrice;
        private int responseTimeHours;
        private int monthlyInteractions;
        private boolean includesVideoCalls;
        private boolean personalizedPlan;
        private boolean prioritySupport;

        public Builder tier(String tier) { this.tier = tier; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder monthlyPrice(double monthlyPrice) { this.monthlyPrice = monthlyPrice; return this; }
        public Builder responseTimeHours(int responseTimeHours) { this.responseTimeHours = responseTimeHours; return this; }
        public Builder monthlyInteractions(int monthlyInteractions) { this.monthlyInteractions = monthlyInteractions; return this; }
        public Builder includesVideoCalls(boolean includesVideoCalls) { this.includesVideoCalls = includesVideoCalls; return this; }
        public Builder personalizedPlan(boolean personalizedPlan) { this.personalizedPlan = personalizedPlan; return this; }
        public Builder prioritySupport(boolean prioritySupport) { this.prioritySupport = prioritySupport; return this; }

        public CoachingTier build() {
            return new CoachingTier(this);
        }
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
