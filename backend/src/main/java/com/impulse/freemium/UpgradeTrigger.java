package com.impulse.freemium;

public class UpgradeTrigger {
    private String type;
    private String metric;
    private String threshold;
    private String frequency;
    private int priority;
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMetric() { return metric; }
    public void setMetric(String metric) { this.metric = metric; }
    public String getThreshold() { return threshold; }
    public void setThreshold(String threshold) { this.threshold = threshold; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
}
