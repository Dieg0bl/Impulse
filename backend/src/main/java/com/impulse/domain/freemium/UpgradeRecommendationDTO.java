package com.impulse.freemium;

public class UpgradeRecommendationDTO {
    private String targetTier;
    private String reason;
    private int priority; // 1 highest

    public String getTargetTier() { return targetTier; }
    public void setTargetTier(String targetTier) { this.targetTier = targetTier; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
}
