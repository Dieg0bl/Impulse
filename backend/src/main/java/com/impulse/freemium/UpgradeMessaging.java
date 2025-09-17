package com.impulse.freemium;

import java.util.List;

public class UpgradeMessaging {
    private String headline;
    private String description;
    private List<String> benefits;
    private String socialProof;
    private String urgency;
    private List<String> visualCues;
    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getBenefits() { return benefits; }
    public void setBenefits(List<String> benefits) { this.benefits = benefits; }
    public String getSocialProof() { return socialProof; }
    public void setSocialProof(String socialProof) { this.socialProof = socialProof; }
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    public List<String> getVisualCues() { return visualCues; }
    public void setVisualCues(List<String> visualCues) { this.visualCues = visualCues; }
}
