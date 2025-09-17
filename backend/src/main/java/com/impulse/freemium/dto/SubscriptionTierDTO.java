package com.impulse.freemium.dto;

import java.util.List;

public class SubscriptionTierDTO {
    private String id;
    private String name;
    private String description;
    private double monthlyPrice;
    private double yearlyPrice;
    private double yearlyDiscount;
    private List<TierFeatureDTO> features;
    private TierLimitsDTO limits;
    private List<TierBenefitDTO> benefits;
    private List<String> targetUsers;
    private List<UpgradeIncentiveDTO> upgradeIncentives;
    private int trialDays;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(double monthlyPrice) { this.monthlyPrice = monthlyPrice; }
    public double getYearlyPrice() { return yearlyPrice; }
    public void setYearlyPrice(double yearlyPrice) { this.yearlyPrice = yearlyPrice; }
    public double getYearlyDiscount() { return yearlyDiscount; }
    public void setYearlyDiscount(double yearlyDiscount) { this.yearlyDiscount = yearlyDiscount; }
    public List<TierFeatureDTO> getFeatures() { return features; }
    public void setFeatures(List<TierFeatureDTO> features) { this.features = features; }
    public TierLimitsDTO getLimits() { return limits; }
    public void setLimits(TierLimitsDTO limits) { this.limits = limits; }
    public List<TierBenefitDTO> getBenefits() { return benefits; }
    public void setBenefits(List<TierBenefitDTO> benefits) { this.benefits = benefits; }
    public List<String> getTargetUsers() { return targetUsers; }
    public void setTargetUsers(List<String> targetUsers) { this.targetUsers = targetUsers; }
    public List<UpgradeIncentiveDTO> getUpgradeIncentives() { return upgradeIncentives; }
    public void setUpgradeIncentives(List<UpgradeIncentiveDTO> upgradeIncentives) { this.upgradeIncentives = upgradeIncentives; }
    public int getTrialDays() { return trialDays; }
    public void setTrialDays(int trialDays) { this.trialDays = trialDays; }
}
