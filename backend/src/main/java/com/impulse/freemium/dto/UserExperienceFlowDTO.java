package com.impulse.freemium.dto;

import java.util.List;

public class UserExperienceFlowDTO {
    private String entry;
    private List<String> demonstration;
    private String comparison;
    private String callToAction;
    private String fallback;

    public String getEntry() { return entry; }
    public void setEntry(String entry) { this.entry = entry; }
    public List<String> getDemonstration() { return demonstration; }
    public void setDemonstration(List<String> demonstration) { this.demonstration = demonstration; }
    public String getComparison() { return comparison; }
    public void setComparison(String comparison) { this.comparison = comparison; }
    public String getCallToAction() { return callToAction; }
    public void setCallToAction(String callToAction) { this.callToAction = callToAction; }
    public String getFallback() { return fallback; }
    public void setFallback(String fallback) { this.fallback = fallback; }
}
