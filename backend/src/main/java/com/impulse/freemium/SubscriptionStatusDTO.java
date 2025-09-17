package com.impulse.freemium;

public class SubscriptionStatusDTO {
    private String currentTier;
    private boolean trialActive;
    private long trialDaysRemaining;
    private String subscriptionStatus;
    private String recommendedUpgrade;

    public String getCurrentTier() { return currentTier; }
    public void setCurrentTier(String currentTier) { this.currentTier = currentTier; }
    public boolean isTrialActive() { return trialActive; }
    public void setTrialActive(boolean trialActive) { this.trialActive = trialActive; }
    public long getTrialDaysRemaining() { return trialDaysRemaining; }
    public void setTrialDaysRemaining(long trialDaysRemaining) { this.trialDaysRemaining = trialDaysRemaining; }
    public String getSubscriptionStatus() { return subscriptionStatus; }
    public void setSubscriptionStatus(String subscriptionStatus) { this.subscriptionStatus = subscriptionStatus; }
    public String getRecommendedUpgrade() { return recommendedUpgrade; }
    public void setRecommendedUpgrade(String recommendedUpgrade) { this.recommendedUpgrade = recommendedUpgrade; }
}
