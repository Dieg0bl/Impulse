package com.impulse.adapters.persistence.coaching;

import jakarta.persistence.*;

@Entity
@Table(name = "coaching_feature")
public class CoachingFeatureEntity {
    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type;

    @Column(name = "unlimited_usage")
    private boolean unlimitedUsage;

    @Column(name = "monthly_quota")
    private Integer monthlyQuota;

    @Column(name = "tier")
    private String tier;

    public CoachingFeatureEntity() {}

    public CoachingFeatureEntity(String id, String name, String description, String type,
                               boolean unlimitedUsage, Integer monthlyQuota, String tier) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.unlimitedUsage = unlimitedUsage;
        this.monthlyQuota = monthlyQuota;
        this.tier = tier;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isUnlimitedUsage() { return unlimitedUsage; }
    public void setUnlimitedUsage(boolean unlimitedUsage) { this.unlimitedUsage = unlimitedUsage; }

    public Integer getMonthlyQuota() { return monthlyQuota; }
    public void setMonthlyQuota(Integer monthlyQuota) { this.monthlyQuota = monthlyQuota; }

    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
}
