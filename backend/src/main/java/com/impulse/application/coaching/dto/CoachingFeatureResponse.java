package com.impulse.application.coaching.dto;

public class CoachingFeatureResponse {
    private final String id;
    private final String name;
    private final String description;
    private final String type;
    private final boolean unlimitedUsage;
    private final Integer monthlyQuota;
    private final String tier;

    public CoachingFeatureResponse(String id, String name, String description, String type,
                                  boolean unlimitedUsage, Integer monthlyQuota, String tier) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.unlimitedUsage = unlimitedUsage;
        this.monthlyQuota = monthlyQuota;
        this.tier = tier;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public boolean isUnlimitedUsage() { return unlimitedUsage; }
    public Integer getMonthlyQuota() { return monthlyQuota; }
    public String getTier() { return tier; }
}
