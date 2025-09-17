package com.impulse.freemium.dto;

import java.util.List;

public class UpgradeOfferDTO {
    private String type;
    private Object value;
    private int duration;
    private List<String> conditions;
    private int expirationDays;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public List<String> getConditions() { return conditions; }
    public void setConditions(List<String> conditions) { this.conditions = conditions; }
    public int getExpirationDays() { return expirationDays; }
    public void setExpirationDays(int expirationDays) { this.expirationDays = expirationDays; }
}
