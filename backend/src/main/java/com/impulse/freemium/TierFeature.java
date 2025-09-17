package com.impulse.freemium;

public class TierFeature {
    private String id;
    private String name;
    private String description;
    private String category;
    private boolean isUnlimited;
    private Integer monthlyQuota;
    private String qualityLevel;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isUnlimited() { return isUnlimited; }
    public void setUnlimited(boolean unlimited) { isUnlimited = unlimited; }
    public Integer getMonthlyQuota() { return monthlyQuota; }
    public void setMonthlyQuota(Integer monthlyQuota) { this.monthlyQuota = monthlyQuota; }
    public String getQualityLevel() { return qualityLevel; }
    public void setQualityLevel(String qualityLevel) { this.qualityLevel = qualityLevel; }
}
