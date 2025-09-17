package com.impulse.freemium.dto;

public class UpgradeIncentiveDTO {
    private String trigger;
    private String condition;
    private String message;
    private Double discount;
    private String urgency;
    private int validityDays;

    public String getTrigger() { return trigger; }
    public void setTrigger(String trigger) { this.trigger = trigger; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    public int getValidityDays() { return validityDays; }
    public void setValidityDays(int validityDays) { this.validityDays = validityDays; }
}
